import { useEffect } from "react";
import { StyleSheet, Text, View } from "react-native";
import Animated, {
  Easing,
  Keyframe,
  useAnimatedStyle,
  useSharedValue,
  withDelay,
  withRepeat,
  withSequence,
  withTiming,
} from "react-native-reanimated";
import Svg, {
  Circle,
  Defs,
  Ellipse,
  Path,
  Polygon,
  RadialGradient,
  Rect,
  Stop,
  Text as SvgText,
} from "react-native-svg";

const SPLASH_DURATION = 3000; // 2 seconds display

// ---------- Keyframes ----------

const carKeyframe = new Keyframe({
  0: { opacity: 0, transform: [{ scale: 0.85 }, { translateY: 20 }] },
  100: {
    opacity: 1,
    transform: [{ scale: 1 }, { translateY: 0 }],
    easing: Easing.out(Easing.cubic),
  },
});

const logoKeyframe = new Keyframe({
  0: { opacity: 0, transform: [{ translateY: 16 }] },
  100: {
    opacity: 1,
    transform: [{ translateY: 0 }],
    easing: Easing.out(Easing.quad),
  },
});

const badgeKeyframe = new Keyframe({
  0: { opacity: 0, transform: [{ scale: 0.2 }] },
  80: { transform: [{ scale: 1.15 }], easing: Easing.out(Easing.back(2)) },
  100: { opacity: 1, transform: [{ scale: 1 }] },
});

const loadingKeyframe = new Keyframe({
  0: { opacity: 0 },
  100: { opacity: 1 },
});

// ---------- Car Illustration ----------

function CarIllustration() {
  return (
    <Svg width={240} height={130} viewBox="0 0 240 130">
      <Defs>
        <RadialGradient id="bodyGrad" cx="50%" cy="40%" r="60%">
          <Stop offset="0%" stopColor="#2a3a5c" />
          <Stop offset="100%" stopColor="#0d1525" />
        </RadialGradient>
        <RadialGradient id="wheelGlow" cx="50%" cy="50%" r="50%">
          <Stop offset="0%" stopColor="#1688E5" stopOpacity={0.5} />
          <Stop offset="100%" stopColor="#1688E5" stopOpacity={0} />
        </RadialGradient>
      </Defs>

      {/* Ground shadow */}
      <Ellipse
        cx={120}
        cy={116}
        rx={100}
        ry={8}
        fill="#1688E5"
        fillOpacity={0.12}
      />

      {/* Car body */}
      <Rect
        x={30}
        y={72}
        width={180}
        height={32}
        rx={6}
        fill="url(#bodyGrad)"
        stroke="#1e3060"
        strokeWidth={1}
      />
      <Path
        d="M55 72 Q70 48 90 44 L150 44 Q170 48 185 72 Z"
        fill="url(#bodyGrad)"
        stroke="#1e3060"
        strokeWidth={1}
      />

      {/* Windshield / roof pillars */}
      <Path
        d="M88 44 L92 72 M148 44 L148 72"
        stroke="#1a2a4a"
        strokeWidth={1}
      />

      {/* Window */}
      <Rect
        x={94}
        y={46}
        width={52}
        height={24}
        rx={3}
        fill="#0c1832"
        stroke="#1a2e52"
        strokeWidth={0.5}
      />
      <Rect
        x={95}
        y={47}
        width={50}
        height={22}
        rx={2}
        fill="#0a1428"
        fillOpacity={0.9}
      />

      {/* Headlights */}
      <Path
        d="M40 76 Q36 80 35 88 L45 88 Q44 80 42 76 Z"
        fill="#1688E5"
        fillOpacity={0.9}
      />
      <Path
        d="M40 76 Q36 80 35 88 L45 88 Q44 80 42 76 Z"
        fill="#00cfff"
        fillOpacity={0.45}
      />
      <Path
        d="M200 76 Q204 80 205 88 L195 88 Q196 80 198 76 Z"
        fill="#1688E5"
        fillOpacity={0.9}
      />
      <Path
        d="M200 76 Q204 80 205 88 L195 88 Q196 80 198 76 Z"
        fill="#00cfff"
        fillOpacity={0.45}
      />

      {/* Rocker line */}
      <Path d="M30 88 L210 88" stroke="#1e3060" strokeWidth={0.5} />

      {/* Rear wheel */}
      <Circle
        cx={70}
        cy={100}
        r={14}
        fill="#0d1525"
        stroke="#1a2e52"
        strokeWidth={2}
      />
      <Circle cx={70} cy={100} r={9} fill="#111c32" />
      <Circle
        cx={70}
        cy={100}
        r={5}
        fill="#0d1525"
        stroke="#1688E5"
        strokeWidth={1.5}
      />
      <Circle cx={70} cy={100} r={2} fill="#1688E5" />
      <Ellipse
        cx={70}
        cy={100}
        rx={14}
        ry={14}
        fill="url(#wheelGlow)"
        fillOpacity={0.6}
      />

      {/* Front wheel */}
      <Circle
        cx={170}
        cy={100}
        r={14}
        fill="#0d1525"
        stroke="#1a2e52"
        strokeWidth={2}
      />
      <Circle cx={170} cy={100} r={9} fill="#111c32" />
      <Circle
        cx={170}
        cy={100}
        r={5}
        fill="#0d1525"
        stroke="#1688E5"
        strokeWidth={1.5}
      />
      <Circle cx={170} cy={100} r={2} fill="#1688E5" />
      <Ellipse
        cx={170}
        cy={100}
        rx={14}
        ry={14}
        fill="url(#wheelGlow)"
        fillOpacity={0.6}
      />
    </Svg>
  );
}

// ---------- Warning Badge ----------

function WarningBadge() {
  return (
    <Svg width={28} height={28} viewBox="0 0 28 28">
      <Polygon
        points="14,2 27,25 1,25"
        fill="#FF9500"
        stroke="#FFB340"
        strokeWidth={1}
      />
      <SvgText
        x={14}
        y={22}
        textAnchor="middle"
        fontSize={14}
        fontWeight="900"
        fill="#1a0800"
      >
        !
      </SvgText>
    </Svg>
  );
}

// ---------- Animated Beam ----------

function HeadlightBeam({ style }: { style?: object }) {
  const opacity = useSharedValue(0.5);

  useEffect(() => {
    opacity.value = withRepeat(
      withSequence(
        withTiming(1, { duration: 900, easing: Easing.inOut(Easing.quad) }),
        withTiming(0.5, { duration: 900, easing: Easing.inOut(Easing.quad) }),
      ),
      -1,
      false,
    );
  }, []);

  const animStyle = useAnimatedStyle(() => ({ opacity: opacity.value }));

  return <Animated.View style={[styles.beam, style, animStyle]} />;
}

// ---------- Progress Bar ----------

function ProgressBar() {
  const width = useSharedValue(0);

  useEffect(() => {
    width.value = withDelay(
      300,
      withTiming(120, { duration: 1600, easing: Easing.out(Easing.quad) }),
    );
  }, []);

  const barStyle = useAnimatedStyle(() => ({ width: width.value }));

  return (
    <View style={styles.progressTrack}>
      <Animated.View style={[styles.progressFill, barStyle]} />
    </View>
  );
}

// ---------- Dot Pulse ----------

function PulsingDot({ delay }: { delay: number }) {
  const opacity = useSharedValue(0);

  useEffect(() => {
    opacity.value = withDelay(
      delay,
      withRepeat(
        withSequence(
          withTiming(1, { duration: 400 }),
          withTiming(0, { duration: 400 }),
          withTiming(0, { duration: 400 }),
        ),
        -1,
        false,
      ),
    );
  }, []);

  const dotStyle = useAnimatedStyle(() => ({ opacity: opacity.value }));
  return <Animated.Text style={[styles.dot, dotStyle]}>.</Animated.Text>;
}

// ---------- Main Screen ----------

interface SplashScreenProps {
  onFinish?: () => void;
}

export default function SplashScreen({ onFinish }: SplashScreenProps) {
  useEffect(() => {
    const timer = setTimeout(() => {
      onFinish?.();
    }, SPLASH_DURATION);
    return () => clearTimeout(timer);
  }, [onFinish]);

  return (
    <View style={styles.container}>
      {/* Subtle grid overlay */}
      <View style={styles.gridOverlay} pointerEvents="none" />

      {/* Background glow */}
      <View style={styles.bgGlow} pointerEvents="none" />

      {/* Car + warning badge */}
      <Animated.View
        entering={carKeyframe.duration(900)}
        style={styles.carContainer}
      >
        <HeadlightBeam style={styles.beamLeft} />
        <HeadlightBeam style={styles.beamRight} />
        <CarIllustration />
        <Animated.View
          entering={badgeKeyframe.duration(500).delay(600)}
          style={styles.warningBadge}
        >
          <WarningBadge />
        </Animated.View>
      </Animated.View>

      {/* Logo */}
      <Animated.View
        entering={logoKeyframe.duration(700).delay(300)}
        style={styles.logoArea}
      >
        <View style={styles.brandRow}>
          <Text style={styles.brandCar}>CAR</Text>
          <Text style={styles.brandDiag}> DIAG</Text>
        </View>
        <View style={styles.brandDivider} />
        <Text style={styles.tagline}>Smart Diagnosis. Better Driving.</Text>
      </Animated.View>

      {/* Loading indicator */}
      <Animated.View
        entering={loadingKeyframe.duration(400).delay(1100)}
        style={styles.loadingArea}
      >
        <ProgressBar />
        <View style={styles.loadingTextRow}>
          <Text style={styles.loadingText}>Loading</Text>
          <PulsingDot delay={0} />
          <PulsingDot delay={200} />
          <PulsingDot delay={400} />
        </View>
      </Animated.View>
    </View>
  );
}

// ---------- Styles ----------

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#080d1f",
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 24,
    paddingBottom: 32,
    position: "relative",
  },
  gridOverlay: {
    position: "absolute",
    inset: 0,
    opacity: 0.04,
    // Grid via border trick on each cell isn't feasible in RN;
    // use a very subtle tint instead — a full SVG grid can be added here if desired
    backgroundColor: "transparent",
  },
  bgGlow: {
    position: "absolute",
    width: 320,
    height: 180,
    borderRadius: 160,
    backgroundColor: "transparent",
    top: "35%",
    // A true radial glow needs a library like @shopify/react-native-skia; placeholder:
    borderWidth: 0,
    shadowColor: "#1688E5",
    shadowOffset: { width: 0, height: 0 },
    shadowRadius: 80,
    shadowOpacity: 0.25,
  },
  carContainer: {
    width: 240,
    height: 130,
    position: "relative",
    marginBottom: 16,
    alignItems: "center",
    justifyContent: "center",
  },
  beam: {
    position: "absolute",
    bottom: 22,
    width: 80,
    height: 18,
    borderRadius: 40,
    backgroundColor: "#1688E5",
    opacity: 0.5,
  },
  beamLeft: { left: 20 },
  beamRight: { right: 20 },
  warningBadge: {
    position: "absolute",
    top: -6,
    right: 42,
  },
  logoArea: {
    alignItems: "center",
    marginBottom: 0,
  },
  brandRow: {
    flexDirection: "row",
    alignItems: "baseline",
  },
  brandCar: {
    fontSize: 38,
    fontWeight: "900",
    color: "#FFFFFF",
    letterSpacing: 3,
    lineHeight: 46,
    // fontFamily: 'Orbitron-Bold', // Add when font is loaded
  },
  brandDiag: {
    fontSize: 38,
    fontWeight: "900",
    color: "#1688E5",
    letterSpacing: 3,
    lineHeight: 46,
    // fontFamily: 'Orbitron-Bold',
  },
  brandDivider: {
    width: 56,
    height: 2,
    backgroundColor: "#1688E5",
    marginTop: 6,
    borderRadius: 2,
  },
  tagline: {
    fontSize: 12,
    fontWeight: "600",
    color: "#5a6a8a",
    letterSpacing: 1.5,
    marginTop: 8,
    textTransform: "uppercase",
  },
  loadingArea: {
    position: "absolute",
    bottom: 52,
    alignItems: "center",
    gap: 8,
  },
  progressTrack: {
    width: 120,
    height: 2,
    backgroundColor: "rgba(255,255,255,0.08)",
    borderRadius: 2,
    overflow: "hidden",
  },
  progressFill: {
    height: 2,
    backgroundColor: "#1688E5",
    borderRadius: 2,
  },
  loadingTextRow: {
    flexDirection: "row",
    alignItems: "center",
  },
  loadingText: {
    fontSize: 11,
    color: "#3a4a6a",
    letterSpacing: 2,
    fontWeight: "600",
    textTransform: "uppercase",
  },
  dot: {
    fontSize: 11,
    color: "#3a4a6a",
    fontWeight: "600",
  },
});
