import { ScrollView, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { Collapsible } from "@/components/ui/collapsible";
import { BottomTabInset, Spacing } from "@/constants/theme";

export default function TutorialScreen() {
  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <ThemedView style={styles.header}>
          <ThemedText type="title" style={styles.title}>
            Repair Tutorials
          </ThemedText>
          <ThemedText style={styles.subtitle}>
            Learn how to fix your vehicle
          </ThemedText>
        </ThemedView>

        <ThemedView style={styles.tutorialList}>
          <Collapsible title="🔧 How to Replace Spark Plugs">
            <ThemedText type="small" style={styles.tutorialContent}>
              Spark plugs typically need replacement every 30,000 to 100,000
              miles depending on the type and your vehicle model. Here's how to
              do it yourself:
            </ThemedText>
            <ThemedText type="small" style={styles.tutorialStep}>
              1. Locate your spark plugs under the hood{"\n"}2. Disconnect one
              wire at a time
              {"\n"}3. Remove the old spark plug{"\n"}4. Install the new one
              {"\n"}5. Reconnect the wire
            </ThemedText>
          </Collapsible>

          <Collapsible title="🔋 Battery Maintenance & Replacement">
            <ThemedText type="small" style={styles.tutorialContent}>
              A car battery typically lasts 3-5 years. Signs you need a
              replacement include slow engine cranking, dim lights, or clicking
              sounds.
            </ThemedText>
            <ThemedText type="small" style={styles.tutorialStep}>
              1. Disconnect the negative terminal{"\n"}2. Remove battery
              hold-down clamp
              {"\n"}3. Remove the old battery{"\n"}4. Install the new battery
              {"\n"}5. Reconnect terminals
            </ThemedText>
          </Collapsible>

          <Collapsible title="⚙️ Engine Air Filter Replacement">
            <ThemedText type="small" style={styles.tutorialContent}>
              Replace your engine air filter every 15,000-30,000 miles to
              maintain fuel efficiency and engine performance.
            </ThemedText>
            <ThemedText type="small" style={styles.tutorialStep}>
              1. Locate the air filter box{"\n"}2. Remove the top cover{"\n"}3.
              Take out the old filter{"\n"}4. Insert the new filter{"\n"}5.
              Secure the cover back
            </ThemedText>
          </Collapsible>

          <Collapsible title="🛑 Brake Pad Replacement">
            <ThemedText type="small" style={styles.tutorialContent}>
              Replace brake pads when they wear down to 2-3mm. Warning signs
              include squealing and reduced braking power.
            </ThemedText>
            <ThemedText type="small" style={styles.tutorialStep}>
              1. Raise the vehicle safely{"\n"}2. Remove the wheel{"\n"}3.
              Remove the caliper
              {"\n"}4. Replace the brake pads{"\n"}5. Reinstall and test brakes
            </ThemedText>
          </Collapsible>

          <Collapsible title="💧 Oil Change DIY Guide">
            <ThemedText type="small" style={styles.tutorialContent}>
              Change your oil every 3,000-5,000 miles or as recommended by your
              manufacturer.
            </ThemedText>
            <ThemedText type="small" style={styles.tutorialStep}>
              1. Warm up the engine{"\n"}2. Drain the old oil{"\n"}3. Replace
              oil filter
              {"\n"}4. Add new oil{"\n"}5. Check level with dipstick
            </ThemedText>
          </Collapsible>
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
  tutorialList: {
    gap: Spacing.three,
  },
  tutorialContent: {
    marginBottom: Spacing.two,
    lineHeight: 20,
  },
  tutorialStep: {
    marginTop: Spacing.two,
    lineHeight: 22,
    color: "#aaa",
  },
});
