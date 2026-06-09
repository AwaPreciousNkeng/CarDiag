import { Ionicons } from "@expo/vector-icons";
import { NativeTabs } from "expo-router/unstable-native-tabs";
import { useColorScheme } from "react-native";

import { Colors } from "@/constants/theme";

const SELECTED_COLOR = "#00AAFF";
const UNSELECTED_COLOR = "#666";

interface TabConfig {
  name: string;
  label: string;
  icon: React.ComponentProps<typeof Ionicons>["name"];
}

const tabs: TabConfig[] = [
  { name: "index", label: "Home", icon: "home" },
  { name: "scan", label: "Scan", icon: "scan-circle" },
  { name: "history", label: "History", icon: "time" },
  { name: "tutorial", label: "Tutorial", icon: "book" },
  { name: "people", label: "Profile", icon: "person-circle" },
];

export default function AppTabs() {
  const scheme = useColorScheme();
  const colors = Colors[scheme === "unspecified" ? "light" : scheme];

  return (
    <NativeTabs
      backgroundColor={colors.background}
      indicatorColor={colors.backgroundElement}
      labelStyle={{
        selected: { color: SELECTED_COLOR, fontWeight: "600" },
        unselected: { color: UNSELECTED_COLOR },
      }}
    >
      {tabs.map((tab) => (
        <NativeTabs.Trigger key={tab.name} name={tab.name}>
          <NativeTabs.Trigger.Label>{tab.label}</NativeTabs.Trigger.Label>
          <NativeTabs.Trigger.Icon
            src={<Ionicons name={tab.icon} size={24} color={SELECTED_COLOR} />}
          />
        </NativeTabs.Trigger>
      ))}
    </NativeTabs>
  );
}
