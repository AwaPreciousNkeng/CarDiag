import { ScrollView, StyleSheet, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { BottomTabInset, Spacing } from "@/constants/theme";

export default function ProfileScreen() {
  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <ThemedView style={styles.header}>
          <ThemedText type="title" style={styles.title}>
            Profile
          </ThemedText>
        </ThemedView>

        {/* User Card */}
        <ThemedView type="backgroundElement" style={styles.userCard}>
          <View style={styles.avatar}>
            <ThemedText style={styles.avatarText}>JD</ThemedText>
          </View>
          <View style={styles.userInfo}>
            <ThemedText style={styles.userName}>John Doe</ThemedText>
            <ThemedText style={styles.userEmail}>
              john.doe@example.com
            </ThemedText>
            <ThemedText style={styles.userPhone}>+1 (555) 123-4567</ThemedText>
          </View>
          <ThemedText style={styles.editButton}>✎</ThemedText>
        </ThemedView>

        {/* Vehicles Section */}
        <ThemedView style={styles.section}>
          <ThemedText type="subtitle" style={styles.sectionTitle}>
            My Vehicles
          </ThemedText>
          <ThemedView type="backgroundElement" style={styles.vehicleCard}>
            <View style={styles.vehicleIcon}>
              <ThemedText style={styles.vehicleIconText}>🚗</ThemedText>
            </View>
            <View style={styles.vehicleInfo}>
              <ThemedText style={styles.vehicleName}>Toyota Camry</ThemedText>
              <ThemedText style={styles.vehicleDetails}>
                2018 • 45,000 miles
              </ThemedText>
            </View>
          </ThemedView>

          <ThemedView type="backgroundElement" style={styles.vehicleCard}>
            <View style={styles.vehicleIcon}>
              <ThemedText style={styles.vehicleIconText}>🚙</ThemedText>
            </View>
            <View style={styles.vehicleInfo}>
              <ThemedText style={styles.vehicleName}>Honda CR-V</ThemedText>
              <ThemedText style={styles.vehicleDetails}>
                2020 • 32,000 miles
              </ThemedText>
            </View>
          </ThemedView>
        </ThemedView>

        {/* Settings Section */}
        <ThemedView style={styles.section}>
          <ThemedText type="subtitle" style={styles.sectionTitle}>
            Settings
          </ThemedText>
          <ThemedView type="backgroundElement" style={styles.settingItem}>
            <ThemedText style={styles.settingLabel}>Notifications</ThemedText>
            <ThemedText style={styles.settingValue}>On</ThemedText>
          </ThemedView>

          <ThemedView type="backgroundElement" style={styles.settingItem}>
            <ThemedText style={styles.settingLabel}>Dark Mode</ThemedText>
            <ThemedText style={styles.settingValue}>On</ThemedText>
          </ThemedView>

          <ThemedView type="backgroundElement" style={styles.settingItem}>
            <ThemedText style={styles.settingLabel}>Language</ThemedText>
            <ThemedText style={styles.settingValue}>English</ThemedText>
          </ThemedView>
        </ThemedView>

        {/* Account Section */}
        <ThemedView style={styles.section}>
          <ThemedText type="subtitle" style={styles.sectionTitle}>
            Account
          </ThemedText>
          <ThemedView type="backgroundElement" style={styles.accountOption}>
            <ThemedText style={styles.accountLabel}>Change Password</ThemedText>
          </ThemedView>

          <ThemedView type="backgroundElement" style={styles.accountOption}>
            <ThemedText style={styles.accountLabel}>Privacy Policy</ThemedText>
          </ThemedView>

          <ThemedView
            type="backgroundElement"
            style={[styles.accountOption, styles.logoutOption]}
          >
            <ThemedText style={styles.logoutLabel}>Logout</ThemedText>
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
  userCard: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: Spacing.four,
    paddingVertical: Spacing.four,
    borderRadius: Spacing.three,
    marginBottom: Spacing.four,
  },
  avatar: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: "#00AAFF",
    justifyContent: "center",
    alignItems: "center",
    marginRight: Spacing.three,
  },
  avatarText: {
    fontSize: 24,
    fontWeight: "bold",
    color: "#fff",
  },
  userInfo: {
    flex: 1,
  },
  userName: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: 4,
  },
  userEmail: {
    fontSize: 12,
    color: "#888",
    marginBottom: 4,
  },
  userPhone: {
    fontSize: 12,
    color: "#888",
  },
  editButton: {
    fontSize: 20,
    paddingLeft: Spacing.two,
  },
  section: {
    marginBottom: Spacing.four,
    gap: Spacing.two,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: Spacing.two,
  },
  vehicleCard: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.three,
    borderRadius: Spacing.two,
  },
  vehicleIcon: {
    width: 50,
    height: 50,
    borderRadius: Spacing.two,
    backgroundColor: "#1a1f3a",
    justifyContent: "center",
    alignItems: "center",
    marginRight: Spacing.three,
  },
  vehicleIconText: {
    fontSize: 28,
  },
  vehicleInfo: {
    flex: 1,
  },
  vehicleName: {
    fontSize: 14,
    fontWeight: "600",
    marginBottom: 4,
  },
  vehicleDetails: {
    fontSize: 12,
    color: "#888",
  },
  settingItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.three,
    borderRadius: Spacing.two,
  },
  settingLabel: {
    fontSize: 14,
    fontWeight: "500",
  },
  settingValue: {
    fontSize: 12,
    color: "#00AAFF",
  },
  accountOption: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.three,
    borderRadius: Spacing.two,
  },
  accountLabel: {
    fontSize: 14,
    fontWeight: "500",
    color: "#888",
  },
  logoutOption: {
    backgroundColor: "#1a2540",
  },
  logoutLabel: {
    fontSize: 14,
    fontWeight: "600",
    color: "#FF4444",
  },
});
