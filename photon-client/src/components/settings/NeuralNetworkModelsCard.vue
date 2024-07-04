<script setup lang="ts">
import { useSettingsStore } from "@/stores/settings/GeneralSettingsStore";
import { Euler, Quaternion as ThreeQuat } from "three";
import type { Quaternion } from "@/types/PhotonTrackingTypes";
import { toDeg } from "@/lib/MathUtils";
import { computed } from "vue";

enum ModelVersion {
  YOLO_V5,
  YOLO_V8
}

enum ModelFormat {
  RKNN
}

interface ModelOnDisk {
  filename: string;
  version: ModelVersion;
  format: ModelFormat;
}

// Hack for testing
const models: ModelOnDisk[] = [
  {
    filename: "hello.rknn",
    version: ModelVersion.YOLO_V5,
    format: ModelFormat.RKNN
  },
  {
    filename: "hello2.rknn",
    version: ModelVersion.YOLO_V8,
    format: ModelFormat.RKNN
  }
];

// Hackery to make pretty strings
const ModelVersionNames = {
  [ModelVersion.YOLO_V5]: "YOLO Version 5",
  [ModelVersion.YOLO_V8]: "YOLO Version 8"
};

const ModelFormatNames = {
  [ModelFormat.RKNN]: "RKNN Format"
};

const prettyModels = computed(() => {
  return models.map((model) => ({
    ...model,
    version: ModelVersionNames[model.version],
    format: ModelFormatNames[model.format]
  }));
});
</script>

<template>
  <v-card dark class="pr-6 pb-3 mb-3" style="background-color: #006492">
    <v-card-title>NN Models</v-card-title>

    <v-data-table
      dense
      style="width: 100%"
      class="pl-2 pr-2"
      :headers="[
        { text: 'Name', value: 'filename' },
        { text: 'Format', value: 'format' },
        { text: 'Model', value: 'version' }
      ]"
      :items="prettyModels"
    />
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
