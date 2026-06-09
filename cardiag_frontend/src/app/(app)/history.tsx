import { ScrollView, StyleSheet, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { BottomTabInset, Spacing } from "@/constants/theme";

export default function HistoryScreen() {
  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <ThemedView style={styles.header}>
          <ThemedText type="title" style={styles.title}>
            Diagnostic History
          </ThemedText>
          <ThemedText style={styles.subtitle}>Past vehicle scans</ThemedText>
        </ThemedView>

        <ThemedView style={styles.filterBar}>
          <ThemedView type="backgroundElement" style={styles.filter}>
            <ThemedText style={styles.filterText}>All Vehicles</ThemedText>
          </ThemedView>
          <ThemedView type="backgroundElement" style={styles.filter}>
            <ThemedText style={styles.filterText}>This Month</ThemedText>
          </ThemedView>
        </ThemedView>

        <ThemedView style={styles.historyList}>
          <ThemedView type="backgroundElement" style={styles.historyItem}>
            <View style={styles.itemHeader}>
              <ThemedText style={styles.itemTitle}>P0301 - Misfire</ThemedText>
              <ThemedText style={styles.itemDate}>May 15, 2024</ThemedText>
            </View>
            <ThemedText style={styles.itemDescription}>
              Cylinder 1 misfire detected
            </ThemedText>
            <View style={styles.itemSeverity}>
              <ThemedText style={styles.severityMedium}>
                Medium Severity
              </ThemedText>
            </View>
          </ThemedView>

          <ThemedView type="backgroundElement" style={styles.historyItem}>
            <View style={styles.itemHeader}>
              <ThemedText style={styles.itemTitle}>
                P0101 - Mass Air Flow
              </ThemedText>
              <ThemedText style={styles.itemDate}>May 10, 2024</ThemedText>
            </View>
            <ThemedText style={styles.itemDescription}>
              Mass air flow sensor error
            </ThemedText>
            <View style={styles.itemSeverity}>
              <ThemedText style={styles.severityHigh}>High Severity</ThemedText>
            </View>
          </ThemedView>

          <ThemedView type="backgroundElement" style={styles.historyItem}>
            <View style={styles.itemHeader}>
              <ThemedText style={styles.itemTitle}>
                Engine Status Check
              </ThemedText>
              <ThemedText style={styles.itemDate}>May 05, 2024</ThemedText>
            </View>
            <ThemedText style={styles.itemDescription}>
              No issues detected - healthy engine
            </ThemedText>
            <View style={styles.itemSeverity}>
              <ThemedText style={styles.severityGood}>Good</ThemedText>
            </View>
          </ThemedView>
        </ThemedView>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#0a0e27",
  },
  scrollContent: {
    flexGrow: 1,
    paddingHorizontal: Spacing.four,
    paddingBottom: BottomTabInset + Spacing.three,
    paddingTop: Spacing.three,
  },
  header: {
    marginBottom: Spacing.four,
  },
  title: {
    marginBottom: Spacing.two,
  },
  subtitle: {
    fontSize: 14,
    color: "#888",
  },
  filterBar: {
    flexDirection: "row",
    gap: Spacing.two,
    marginBottom: Spacing.four,
  },
  filter: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    borderRadius: Spacing.two,
  },
  filterText: {
    fontSize: 12,
    fontWeight: "600",
  },
  historyList: {
    gap: Spacing.three,
  },
  historyItem: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.three,
    borderRadius: Spacing.two,
    gap: Spacing.two,
  },
  itemHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  itemTitle: {
    fontSize: 14,
    fontWeight: "600",
    flex: 1,
  },
  itemDate: {
    fontSize: 12,
    color: "#888",
  },
  itemDescription: {
    fontSize: 12,
    color: "#aaa",
  },
  itemSeverity: {
    marginTop: Spacing.two,
  },
  severityMedium: {
    color: "#FFA500",
    fontSize: 11,
    fontWeight: "600",
  },
  severityHigh: {
    color: "#FF4444",
    fontSize: 11,
    fontWeight: "600",
  },
  severityGood: {
    color: "#44FF44",
    fontSize: 11,
    fontWeight: "600",
  },
});
