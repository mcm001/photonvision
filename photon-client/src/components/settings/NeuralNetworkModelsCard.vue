<script setup lang="ts">
import { computed, ref } from "vue";

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

const importNewModel = ref();
const openImportNewModelPrompt = () => {
  importNewModel.value.click();
};

const handleImportModel = async () => {
  const files = importNewModel.value.files;
  if (files.length === 0) return;
  const uploadedModel = files[0];

  // todo
  console.log(uploadedModel);
};

const allowedFileTypes = (format: ModelFormat | null) => {
  if (format === ModelFormat.RKNN) {
    return ".rknn";
  }

  return "*";
};

// temp things for the dialog
const showImportDialog = ref(false);
const importModelFile = ref<File | null>(null);
const importModelFormat = ref<ModelFormat | null>(0);
const importModelVersion = ref<ModelVersion | null>(0);
const importModelClasses = ref<string | null>(null);
</script>

<template>
  <v-card dark class="pr-6 pb-3 mb-3 mt-6" style="background-color: #006492">
    <v-row>
      <v-col cols="12" md="8">
        <v-card-title>NN Models</v-card-title>
      </v-col>
      <v-col>
        <v-btn color="secondary" @click="() => (showImportDialog = true)">
          <v-icon left class="open-icon"> mdi-import </v-icon>
          <span class="open-label">Import New Model</span>
        </v-btn>

        <!-- TODO (Matt): Only accepts *rknn files -->
        <!-- <input ref="importNewModel" type="file" accept=".rknn" style="display: none" @change="importModel" /> -->
      </v-col>
    </v-row>

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
          <v-row class="mt-6 ml-4">
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

            <pv-input
              v-model="importModelClasses"
              label="Class names"
              tooltip="Enter the Team Number or the IP address of the NetworkTables Server"
            />
          </v-row>

          <v-row class="mt-3">
            <v-file-input
              v-model="importModelFile"
              label="Upload Model File"
              :accept="allowedFileTypes(importModelFormat)"
            />
          </v-row>
          <v-row
            class="mt-12 ml-8 mr-8 mb-1"
            style="display: flex; align-items: center; justify-content: center"
            align="center"
          >
            <v-btn color="secondary" :disabled="importModelFile === null" @click="handleImportModel">
              <v-icon left class="open-icon"> mdi-import </v-icon>
              <span class="open-label">Import Settings</span>
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
