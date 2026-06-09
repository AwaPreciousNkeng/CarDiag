import { StyleSheet, Text } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { BottomTabInset, MaxContentWidth, Spacing } from "@/constants/theme";

export default function ScanScreen() {
  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <ThemedText type="title" style={styles.title}>
            Scan Vehicle
          </ThemedText>
          <ThemedText style={styles.subtitle}>
            Start a diagnostic scan
          </ThemedText>
        </ThemedView>

        <ThemedView type="backgroundElement" style={styles.scanBox}>
          <Text style={styles.scanIcon}>📱</Text>
          <ThemedText type="subtitle" style={styles.scanText}>
            Tap to Start Scan
          </ThemedText>
          <ThemedText style={styles.scanDescription}>
            Point your device at the vehicle's diagnostic port
          </ThemedText>
        </ThemedView>

        <ThemedView style={styles.infoSection}>
          <ThemedText type="subtitle" style={styles.sectionTitle}>
            Recent Scans
          </ThemedText>
          <ThemedView type="backgroundElement" style={styles.scanItem}>
            <ThemedText>No recent scans</ThemedText>
          </ThemedView>
        </ThemedView>
      </SafeAreaView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    flexDirection: "row",
  },
  safeArea: {
    flex: 1,
    paddingHorizontal: Spacing.four,
    alignItems: "center",
    gap: Spacing.three,
    paddingBottom: BottomTabInset + Spacing.three,
    maxWidth: MaxContentWidth,
    width: "100%",
  },
  header: {
    alignItems: "center",
    paddingVertical: Spacing.four,
  },
  title: {
    textAlign: "center",
    marginBottom: Spacing.two,
  },
  subtitle: {
    textAlign: "center",
    fontSize: 14,
  },
  scanBox: {
    width: "100%",
    paddingVertical: Spacing.six,
    paddingHorizontal: Spacing.four,
    borderRadius: Spacing.four,
    alignItems: "center",
    justifyContent: "center",
    gap: Spacing.two,
    marginVertical: Spacing.four,
  },
  scanIcon: {
    fontSize: 60,
    marginBottom: Spacing.two,
  },
  scanText: {
    textAlign: "center",
  },
  scanDescription: {
    textAlign: "center",
    fontSize: 12,
    color: "#888",
    marginTop: Spacing.two,
  },
  infoSection: {
    width: "100%",
    gap: Spacing.three,
  },
  sectionTitle: {
    paddingHorizontal: Spacing.two,
    fontSize: 16,
  },
  scanItem: {
    paddingVertical: Spacing.three,
    paddingHorizontal: Spacing.three,
    borderRadius: Spacing.two,
    alignItems: "center",
  },
});
