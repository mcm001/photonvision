<script setup lang="ts">
import { computed, ref } from "vue";
import { useStateStore } from "@/stores/StateStore";
import axios from "axios";

enum ModelVersion {
  YOLO_V5,
  YOLO_V8
}

enum ModelFormat {
  RKNN,
  ONNX
}

interface ModelOnDisk {
  filename: string;
  version: ModelVersion;
  format: ModelFormat;
  classNames: string[];
}

// Hack for testing
const models: ModelOnDisk[] = [
  {
    filename: "hello.rknn",
    version: ModelVersion.YOLO_V5,
    format: ModelFormat.RKNN,
    classNames: ["note"]
  },
  {
    filename: "hello2.rknn",
    version: ModelVersion.YOLO_V8,
    format: ModelFormat.RKNN,
    classNames: ["note", "red robot", "blue robot"]
  }
];

// Hackery to make pretty strings
const ModelVersionNames = {
  [ModelVersion.YOLO_V5]: "YOLO Version 5",
  [ModelVersion.YOLO_V8]: "YOLO Version 8"
};

const ModelFormatNames = {
  [ModelFormat.RKNN]: "RKNN Format",
  [ModelFormat.ONNX]: "ONNX Format"
};

const prettyModels = computed(() => {
  return models.map((model) => ({
    ...model,
    version: ModelVersionNames[model.version],
    format: ModelFormatNames[model.format],
    classNames: `[${model.classNames.join(", ")}]`
  }));
});

const handleImportModel = async () => {
  const file = importModelFile.value;
  if (!file) {
    //todo: error
    return;
  }

  const params = {
    format: importModelFormat.value,
    version: importModelVersion.value,
    classes: importModelClasses.value!.split(",").map((it) => it.trim()),
    numClasses: importModelNumClasses.value
  };

  const formData = new FormData();
  formData.append("model", file);
  formData.append("params", JSON.stringify(params));

  useStateStore().showSnackbarMessage({
    message: "New Software Upload in Progress...",
    color: "secondary",
    timeout: -1
  });

  axios
    .post("nnModelImport", formData, {
      headers: { "Content-Type": "multipart/form-data" },
      onUploadProgress: ({ progress }) => {
        const uploadPercentage = (progress || 0) * 100.0;
        if (uploadPercentage < 99.5) {
          useStateStore().showSnackbarMessage({
            message: "Uploading new model, " + uploadPercentage.toFixed(2) + "% complete",
            color: "secondary",
            timeout: -1
          });
        } else {
          useStateStore().showSnackbarMessage({
            message: "Processing new model...",
            color: "secondary",
            timeout: -1
          });
        }
      }
    })
    .then((response) => {
      useStateStore().showSnackbarMessage({
        message: response.data.text || response.data,
        color: "success"
      });
    })
    .catch((error) => {
      if (error.response) {
        useStateStore().showSnackbarMessage({
          color: "error",
          message: error.response.data.text || error.response.data
        });
      } else if (error.request) {
        useStateStore().showSnackbarMessage({
          color: "error",
          message: "Error while trying to process the request! The backend didn't respond."
        });
      } else {
        useStateStore().showSnackbarMessage({
          color: "error",
          message: "An error occurred while trying to process the request."
        });
      }
    });
};

const allowedFileTypes = (format: ModelFormat | null) => {
  if (format === ModelFormat.RKNN) {
    return ".rknn";
  }
  if (format === ModelFormat.ONNX) {
    return ".onnx";
  }

  return "*";
};

const checkNumClasses = (classes: string | null): string | boolean => {
  if (!classes) return "Class list must be a comma-seperated list!";
  if (classes.split(",").length === importModelNumClasses.value) return true;
  return `Expected exactly ${importModelNumClasses.value} comma-seperated numbers!`;
};

// temp things for the dialog
const showImportDialog = ref(false);
const importModelFile = ref<File | null>(null);
const importModelFormat = ref<ModelFormat | null>(0);
const importModelVersion = ref<ModelVersion | null>(0);
const importModelNumClasses = ref<int | null>(null);
const importModelClasses = ref<string | null>(null);
</script>

<template>
  <v-card dark class="pr-6 pb-3 mb-3 mt-6" style="background-color: #006492">
    <div style="display: flex; flex-wrap: wrap">
      <v-card-title>NN Models</v-card-title>
      <v-btn style="margin-left: auto" class="mt-4" color="secondary" @click="() => (showImportDialog = true)">
        <v-icon left class="open-icon"> mdi-import </v-icon>
        <span class="open-label">Import New Model</span>
      </v-btn>
    </div>

    <v-data-table
      dense
      style="width: 100%"
      class="pl-2 pr-2"
      :headers="[
        { text: 'Name', value: 'filename' },
        { text: 'Format', value: 'format' },
        { text: 'Version', value: 'version' },
        { text: 'Classes', value: 'classNames' }
      ]"
      :items="prettyModels"
    />

    <v-dialog
      v-model="showImportDialog"
      width="600"
      @input="
        () => {
          importModelFile = null;
          importModelFormat = null;
          importModelVersion = null;
        }
      "
    >
      <v-card color="primary" dark>
        <v-card-title>Import New Model</v-card-title>
        <v-card-text>
          Upload and apply previously saved or exported PhotonVision settings to this device

          <div>
            <pv-select
              v-model="importModelFormat"
              label="Model File Format"
              tooltip="Select the type of model file"
              :items="Object.values(ModelFormatNames)"
              :select-cols="8"
              style="width: 100%"
            />
            <pv-select
              v-model="importModelVersion"
              label="Model Type"
              tooltip="Select the type of model"
              :items="Object.values(ModelVersionNames)"
              :select-cols="8"
              style="width: 100%"
            />
            <pv-number-input
              v-model="importModelNumClasses"
              label="Num classes"
              tooltip="Number of classes this model was trained for. This must exactly match the number used during training!"
              :label-cols="4"
            />
            <pv-input
              v-model="importModelClasses"
              label="Class names"
              tooltip="Enter a comma-seperated list of classes. Must have exactly as many classes as this model was trained for!"
              placeholder="apple, note, red robot"
              :input-cols="8"
              :rules="[(v) => checkNumClasses(v)]"
            />
          </div>

          <div class="mt-3">
            <v-file-input
              v-model="importModelFile"
              label="Upload Model File"
              :accept="allowedFileTypes(importModelFormat)"
            />
          </div>
          <v-row
            class="mt-8 ml-6 mr-6 mb-1"
            style="display: flex; align-items: center; justify-content: center"
            align="center"
          >
            <v-btn color="secondary" :disabled="importModelFile === null" @click="handleImportModel">
              <v-icon left class="open-icon"> mdi-import </v-icon>
              <span class="open-label">Import Model</span>
            </v-btn>
          </v-row>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<style scoped lang="scss">
.v-data-table {
  width: 100%;
  height: 100%;
  text-align: center;
  background-color: #006492 !important;

  th,
  td {
    background-color: #006492 !important;
    font-size: 1rem !important;
    color: white !important;
  }

  td {
    font-family: monospace !important;
  }

  tbody :hover td {
    background-color: #005281 !important;
  }

  ::-webkit-scrollbar {
    width: 0;
    height: 0.55em;
    border-radius: 5px;
  }

  ::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
    border-radius: 10px;
  }

  ::-webkit-scrollbar-thumb {
    background-color: #ffd843;
    border-radius: 10px;
  }
}
</style>
