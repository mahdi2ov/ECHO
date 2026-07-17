// User session and theme, persisted in localStorage since every page

const THEME_KEY = "theme";
const USER_KEY = "user";

export const getUser = () => {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
};

export const getUserId = () => {
  const user = getUser();
  if (!user) {
    return null;
  }
  if (user.id === undefined || user.id === null) {
    return null;
  }
  return user.id;
};

export const setUser = (user) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

export const clearUser = () => {
  localStorage.removeItem(USER_KEY);
};

export const isLoggedIn = () => {
  return !!getUser();
};

export const getTheme = () => {
  return localStorage.getItem(THEME_KEY);
};

export const setTheme = (theme) => {
  localStorage.setItem(THEME_KEY, theme);
};
