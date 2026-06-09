import { DarkTheme, DefaultTheme, Stack, ThemeProvider } from "expo-router";
import { useColorScheme } from "react-native";

import { AuthProvider, useAuth } from "@/contexts/auth-context";
import SplashScreen from "@/screens/splash-screen";

function RootLayoutNav() {
  const colorScheme = useColorScheme();
  const { isSignedIn, isLoading } = useAuth();

  if (isLoading) {
    return <SplashScreen />;
  }

  return (
    <ThemeProvider value={colorScheme === "dark" ? DarkTheme : DefaultTheme}>
      <Stack
        screenOptions={{
          headerShown: false,
          cardStyle: { backgroundColor: "#0a0e27" },
        }}
      >
        {isSignedIn ? (
          <Stack.Screen
            name="(app)"
            options={{
              animationEnabled: false,
            }}
          />
        ) : (
          <Stack.Screen
            name="(auth)"
            options={{
              animationEnabled: false,
            }}
          />
        )}
      </Stack>
    </ThemeProvider>
  );
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <RootLayoutNav />
    </AuthProvider>
  );
}
