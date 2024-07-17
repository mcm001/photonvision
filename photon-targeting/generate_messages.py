#!/usr/bin/env python3

import argparse
import copy
import hashlib
import os
import sys
from pathlib import Path
from typing import List, TypedDict, cast

import yaml
from jinja2 import Environment, FileSystemLoader


class SerdeField(TypedDict):
    name: str
    type: str


class MessageType(TypedDict):
    name: str
    fields: List[SerdeField]
    # will be 'shim' if shimmed, and the shims will be set
    shimmed: bool
    java_decode_shim: str
    java_encode_shim: str
    # C++ helpers
    cpp_include: str


def yaml_to_dict(path: str):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    yaml_file_path = os.path.join(script_dir, path)

    with open(yaml_file_path, "r") as file:
        file_dict: dict = yaml.safe_load(file)

    return file_dict


data_types = yaml_to_dict("src/generate/message_data_types.yaml")


# Helper to check if we need to use our own decoder
def is_intrinsic_type(type_str: str):
    ret = type_str in data_types.keys()
    return ret


# Deal with shimmed types
def get_shimmed_filter(message_db):
    def is_shimmed(message_name: str):
        # We don't (yet) support shimming intrinsic types
        if is_intrinsic_type(message_name):
            return False

        message = get_message_by_name(message_db, message_name)
        return "shimmed" in message and message["shimmed"] == True

    return is_shimmed


def get_qualified_cpp_name(message_db: List[MessageType], message_name: str):
    """
    Get the full name of the type encoded. Eg:
      std::optional<photon::TargetCorner>
      std::array<frc::Transform3d>
    """
    pass


def get_message_by_name(message_db: List[MessageType], message_name: str):
    try:
        return next(
            message for message in message_db if message["name"] == message_name
        )
    except StopIteration as e:
        raise Exception("Could not find " + message_name) from e


def get_field_by_name(message: MessageType, field_name: str):
    return next(f for f in message["fields"] if f["name"] == field_name)


def get_message_hash(message_db: List[MessageType], message: MessageType):
    """
    Calculate a unique message hash via MD5 sum. This is a very similar approach to rosmsg, documented:
    http://wiki.ros.org/ROS/Technical%20Overview#Message_serialization_and_msg_MD5_sums

    For non-intrinsic (user-defined) types, replace its type-string with the md5sum of the submessage definition
    """

    # replace the non-intrinsic typename with its hash
    modified_message = copy.deepcopy(message)
    fields_to_hash = [
        field
        for field in modified_message["fields"]
        if not is_intrinsic_type(field["type"])
    ]

    for field in fields_to_hash:
        sub_message = get_message_by_name(message_db, field["type"])
        subhash = get_message_hash(message_db, sub_message)

        # change the type to be our new md5sum
        field["type"] = subhash.hexdigest()

    # base case: message is all intrinsic types
    # Hash a comments-stripped version for message integrity checking
    cleaned_yaml = yaml.dump(modified_message, default_flow_style=False).strip()
    message_hash = hashlib.md5(cleaned_yaml.encode("ascii"))
    return message_hash


def get_includes(db, message: MessageType) -> str:
    includes = []
    for field in message["fields"]:
        if not is_intrinsic_type(field['type']):
            field_msg = get_message_by_name(db, field['type'])

            if 'shimmed' in field_msg and field_msg['shimmed'] == True:
                includes.append(field_msg['cpp_include'])
            else:
                # must be a photon type.
                includes.append(f'"photon/targeting/{field_msg['name']}.h"')

    print(f"For type {message['name']}")
    print(set(includes))
    return sorted(set(includes))


def parse_yaml():
    config = yaml_to_dict("src/generate/messages.yaml")

    return config


def generate_photon_messages(output_root, template_root):
    messages = parse_yaml()

    env = Environment(
        loader=FileSystemLoader(str(template_root)),
        # autoescape=False,
        # keep_trailing_newline=False,
    )

    env.filters["is_intrinsic"] = is_intrinsic_type
    env.filters["is_shimmed"] = get_shimmed_filter(messages)

    # add our custom types
    extended_data_types = data_types.copy()
    for message in messages:
        name = message["name"]
        extended_data_types[name] = {
            "len": -1,
            "java_type": name,
            "cpp_type": "photon::" + name,
        }

    java_output_dir = Path(output_root) / "main/java/org/photonvision/struct"
    java_output_dir.mkdir(parents=True, exist_ok=True)
    cpp_header_dir = Path(output_root) / "main/native/include/photon/struct/"
    cpp_header_dir.mkdir(parents=True, exist_ok=True)
    cpp_source_dir = Path(output_root) / "main/native/cpp/photon/struct/"
    cpp_source_dir.mkdir(parents=True, exist_ok=True)

    for message in messages:

        # don't generate shimmed types
        if get_shimmed_filter(messages)(message["name"]):
            continue

        message = cast(MessageType, message)

        java_name = f"{message['name']}Serde.java"
        cpp_header_name = f"{message['name']}Serde.h"
        cpp_source_name = f"{message['name']}Serde.cpp"

        java_template = env.get_template("Message.java.jinja")
        cpp_header_template = env.get_template("ThingSerde.h.jinja")
        cpp_source_template = env.get_template("ThingSerde.cpp.jinja")

        message_hash = get_message_hash(messages, message)

        for output_name, template, output_folder in [
            [java_name, java_template, java_output_dir],
            [cpp_header_name, cpp_header_template, cpp_header_dir],
            [cpp_source_name, cpp_source_template, cpp_source_dir],
        ]:
            # Hack in our message getter
            template.globals["get_message_by_name"] = lambda name: get_message_by_name(
                messages, name
            )
            template.globals["get_qualified_name"] = lambda name: get_qualified_cpp_name(
                messages, name
            )

            output_file = output_folder / output_name
            output_file.write_text(
                template.render(
                    message,
                    type_map=extended_data_types,
                    message_str=message,
                    message_hash=message_hash.hexdigest(),
                    cpp_includes=get_includes(messages, message)
                ),
                encoding="utf-8",
            )


def main(argv):
    script_path = Path(__file__).resolve()
    dirname = script_path.parent

    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--output_directory",
        help="Optional. If set, will output the generated files to this directory, otherwise it will use a path relative to the script",
        default=dirname / "src/generated",
        type=Path,
    )
    parser.add_argument(
        "--template_root",
        help="Optional. If set, will use this directory as the root for the jinja templates",
        default=dirname / "src/generate",
        type=Path,
    )
    args = parser.parse_args(argv)

    generate_photon_messages(args.output_directory, args.template_root)


if __name__ == "__main__":
    main(sys.argv[1:])
