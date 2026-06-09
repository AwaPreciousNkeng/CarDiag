# Authentication & Splash Screen Setup

## Overview

This document describes the authentication system and splash screen implementation for the CarDiag mobile application.

## Project Structure

```
src/
├── app/
│   ├── _layout.tsx              # Root layout with AuthProvider
│   ├── (auth)/
│   │   ├── _layout.tsx          # Auth navigation layout
│   │   ├── login.tsx            # Login screen
│   │   └── register.tsx         # Register screen
│   └── (app)/
│       ├── _layout.tsx          # App layout (shows AnimatedSplashOverlay + AppTabs)
│       ├── index.tsx            # Dashboard/Home screen
│       └── explore.tsx          # Explore screen
├── contexts/
│   └── auth-context.tsx         # Auth context provider & hook
└── screens/
    └── splash-screen.tsx        # Splash screen component
```

## Key Components

### 1. **Auth Context** (`src/contexts/auth-context.tsx`)

- **Purpose**: Centralized authentication state management
- **Features**:
  - Tracks user authentication state
  - Manages loading state for splash screen
  - Provides sign up, sign in, and sign out functions
  - Error handling with `error` state and `clearError()` method

**Key Types**:

```typescript
export interface User {
  id: string;
  email: string;
  fullName: string;
  phone?: string;
  vehicle?: string;
  createdAt: string;
}

export interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  isSignedIn: boolean;
  signUp: (email: string, password: string, fullName: string) => Promise<void>;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  error: string | null;
  clearError: () => void;
}
```

**Usage**:

```typescript
import { useAuth } from "@/contexts/auth-context";

export default function MyComponent() {
  const { user, isSignedIn, signIn, signOut } = useAuth();
  // Use auth context...
}
```

### 2. **Root Layout** (`src/app/_layout.tsx`)

- **Purpose**: Handles navigation flow based on authentication state
- **Features**:
  - Wraps entire app with `AuthProvider`
  - Shows `SplashScreen` while loading
  - Conditionally routes to either `(auth)` or `(app)` based on `isSignedIn` state
  - Uses Expo Router's conditional rendering

### 3. **Auth Group** (`src/app/(auth)/`)

- **Purpose**: Groups authentication-related screens
- **Screens**:
  - **Login** (`login.tsx`): Email/password login with Google sign-in option
  - **Register** (`register.tsx`): Account creation with full name, email, password validation

**Features**:

- Input validation (email format, password length, password matching)
- Password visibility toggle
- Loading states with activity indicators
- Error handling and alerts
- Navigation between login and register screens
- Form field styling matching design mockups

### 4. **App Group** (`src/app/(app)/`)

- **Purpose**: Groups authenticated app screens
- **Screens**:
  - **Home/Dashboard** (`index.tsx`): Main dashboard screen
  - **Explore** (`explore.tsx`): Feature exploration screen

### 5. **Splash Screen** (`src/screens/splash-screen.tsx`)

- **Purpose**: Initial loading screen with CarDiag branding
- **Features**:
  - Animated logo entrance
  - "Smart Diagnosis. Better Driving." subtitle
  - "Loading..." indicator
  - Dark theme styling matching app design

## Navigation Flow

```
┌─────────────────────────────────────┐
│         Root Layout (_layout)       │
│      [Wrapped with AuthProvider]    │
└──────────────────┬──────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
    isLoading=true       isLoading=false
        │                     │
        ▼                     ▼
  ┌──────────────┐    ┌─────────────────┐
  │ SplashScreen │    │ Check isSignedIn│
  └──────────────┘    └────────┬────────┘
                               │
                   ┌───────────┴───────────┐
                   │                       │
                isSignedIn=false    isSignedIn=true
                   │                       │
                   ▼                       ▼
              ┌────────┐            ┌──────────┐
              │ (auth) │            │  (app)   │
              │ group  │            │  group   │
              └────┬───┘            └──────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
    ┌────────┐          ┌──────────┐
    │ Login  │◄────────►│ Register │
    └────────┘          └──────────┘
```

## Authentication Flow

### Sign Up

1. User navigates to Register screen
2. Fills in: Full Name, Email, Password, Confirm Password
3. Agrees to Terms & Conditions
4. Submits form
5. `signUp()` is called with user data
6. On success: User is created and stored in context
7. App navigates to authenticated routes `(app)`

### Sign In

1. User enters Email and Password
2. Submits form
3. `signIn()` is called with credentials
4. On success: User is loaded and stored in context
5. App navigates to authenticated routes `(app)`

### Sign Out

1. User triggers logout action (to be implemented in app screens)
2. `signOut()` is called
3. User state is cleared
4. App navigates back to `(auth)` routes

## TODO: Integration Points

### 1. Backend API Integration

In `src/contexts/auth-context.tsx`, replace mock implementations with real API calls:

```typescript
// Replace this:
console.log("Sign in:", { email, password });

// With actual API call:
const response = await fetch("https://api.cardiag.com/auth/signin", {
  method: "POST",
  body: JSON.stringify({ email, password }),
});
```

### 2. Secure Token Storage

Currently, tokens are not persisted. Implement using AsyncStorage or react-native-keychain:

```typescript
import AsyncStorage from "@react-native-async-storage/async-storage";

// Store token
await AsyncStorage.setItem("userToken", token);

// Retrieve token on app launch
const token = await AsyncStorage.getItem("userToken");
```

### 3. Initial Auth Check

In `bootstrapAsync()` function, check if user has valid token:

```typescript
const token = await AsyncStorage.getItem("userToken");
if (token) {
  // Validate token with backend
  const userData = await validateToken(token);
  setUser(userData);
}
```

### 4. Google Sign-In

Implement Google authentication using `expo-auth-session`:

```typescript
import * as Google from "expo-auth-session/providers/google";
```

### 5. Logout Functionality

Add logout button to authenticated screens:

```typescript
<TouchableOpacity onPress={async () => {
  await signOut();
  // Navigation happens automatically via context change
}}>
  <Text>Logout</Text>
</TouchableOpacity>
```

## Styling & Theme

All components use the following design system:

**Colors**:

- Background: `#0a0e27` (dark)
- Primary: `#00AAFF` (cyan/blue)
- Text: `#fff` (white)
- Secondary Text: `#888` (gray)
- Input Background: `#1a1f3a`
- Border: `#333`

**Font Sizes**:

- Title: 28px, bold
- Subtitle: 14px
- Input: 14px, medium weight
- Button: 16px, bold

## Testing

### Test Sign Up

1. Tap "Register" on login screen
2. Fill in all fields with valid data
3. Agree to terms
4. Tap "Register"
5. Should navigate to app screens

### Test Sign In

1. Enter email and password
2. Tap "Login"
3. Should navigate to app screens

### Test Splash Screen

1. Kill and restart app
2. Should see animated splash screen for ~1 second
3. Then navigates to (auth) or (app) based on auth state

## Configuration

### App Entry Point (`app.json`)

Already configured with:

- Dark theme (automatic)
- Splash screen plugin with CarDiag branding
- Expo Router enabled
- TypeScript support

### Expo Router Groups

- `(auth)` - Protected routes for unauthenticated users
- `(app)` - Protected routes for authenticated users

Groups prevent users from back-navigating between auth/app stacks.

## Next Steps

1. ✅ Create splash screen
2. ✅ Create auth screens (login/register)
3. ✅ Set up auth context
4. ✅ Configure navigation flow
5. ⏳ Connect to backend API
6. ⏳ Implement token storage
7. ⏳ Add Google Sign-In
8. ⏳ Add password reset functionality
9. ⏳ Add email verification
10. ⏳ Add biometric authentication

## Dependencies

Make sure these are installed in `package.json`:

- `expo-router` - File-based routing
- `expo-auth-session` - OAuth support (for Google Sign-In)
- `react-native-safe-area-context` - Safe area handling
- `react-native-reanimated` - Animations
- `@react-native-async-storage/async-storage` - Token storage (to add)
- `react-native-keychain` - Secure token storage (optional, to add)

## Troubleshooting

### Issue: "useAuth must be used within an AuthProvider"

**Solution**: Ensure component is nested within the app (inside `RootLayout`)

### Issue: Splash screen doesn't show

**Solution**: Check that `isLoading` state in auth context is being managed correctly

### Issue: Navigation not working between (auth) and (app)

**Solution**: Verify that `isSignedIn` state is properly updated after sign in/out

## References

- [Expo Router Documentation](https://docs.expo.dev/router/introduction/)
- [Expo Auth Session](https://docs.expo.dev/modules/auth-session/)
- [React Native Safe Area Context](https://react-native-safe-area-context.dev/)
