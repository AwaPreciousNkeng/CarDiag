import { Ionicons } from "@expo/vector-icons";
import React from "react";

const SELECTED_COLOR = "#00AAFF";
const UNSELECTED_COLOR = "#666";

export interface TabIconProps {
  name: React.ComponentProps<typeof Ionicons>["name"];
  size?: number;
  isSelected?: boolean;
}

export function TabIcon({ name, size = 24, isSelected = true }: TabIconProps) {
  return (
    <Ionicons
      name={name}
      size={size}
      color={isSelected ? SELECTED_COLOR : UNSELECTED_COLOR}
    />
  );
}

export const TAB_ICONS = {
  home: "home",
  scan: "scan-circle",
  history: "time",
  tutorial: "book",
  profile: "person-circle",
} as const;

export const SELECTED_TAB_COLOR = SELECTED_COLOR;
export const UNSELECTED_TAB_COLOR = UNSELECTED_COLOR;
