import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from "react";

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

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Simulate checking if user is already authenticated (e.g., from AsyncStorage or token)
  useEffect(() => {
    const bootstrapAsync = async () => {
      try {
        // TODO: Check if user token exists in AsyncStorage or secure storage
        // const token = await AsyncStorage.getItem('userToken');
        // if (token) {
        //   setUser(userData);
        // }

        // Simulating a delay for demonstration
        await new Promise((resolve) => setTimeout(resolve, 1000));
      } catch (e) {
        console.error("Failed to restore token", e);
      } finally {
        setIsLoading(false);
      }
    };

    bootstrapAsync();
  }, []);

  const authContext: AuthContextType = {
    user,
    isLoading,
    isSignedIn: !!user,
    signUp: async (email: string, password: string, fullName: string) => {
      try {
        setError(null);
        // TODO: Implement actual sign up API call
        console.log("Sign up:", { email, password, fullName });

        // Simulating sign up
        const newUser: User = {
          id: Math.random().toString(),
          email,
          fullName,
          createdAt: new Date().toISOString(),
        };

        setUser(newUser);
        // TODO: Store token in secure storage
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : "Sign up failed";
        setError(errorMessage);
        throw err;
      }
    },
    signIn: async (email: string, password: string) => {
      try {
        setError(null);
        // TODO: Implement actual sign in API call
        console.log("Sign in:", { email, password });

        // Simulating sign in
        const user: User = {
          id: Math.random().toString(),
          email,
          fullName: "User",
          createdAt: new Date().toISOString(),
        };

        setUser(user);
        // TODO: Store token in secure storage
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : "Sign in failed";
        setError(errorMessage);
        throw err;
      }
    },
    signOut: async () => {
      try {
        setError(null);
        // TODO: Implement actual sign out (API call to invalidate token, etc.)
        setUser(null);
        // TODO: Clear token from secure storage
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : "Sign out failed";
        setError(errorMessage);
        throw err;
      }
    },
    error,
    clearError: () => setError(null),
  };

  return (
    <AuthContext.Provider value={authContext}>{children}</AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
