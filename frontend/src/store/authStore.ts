import { createSignal } from 'solid-js';

// Get token from local storage if exists
const storedToken = localStorage.getItem('token');
let initialPayload = null;

if (storedToken) {
  try {
    const base64Url = storedToken.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    initialPayload = JSON.parse(jsonPayload);
  } catch (e) {
    localStorage.removeItem('token');
  }
}

export const [token, setToken] = createSignal<string | null>(storedToken);
export const [user, setUser] = createSignal<any>(initialPayload);

export const login = (newToken: string) => {
  localStorage.setItem('token', newToken);
  setToken(newToken);
  
  const base64Url = newToken.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
  }).join(''));
  setUser(JSON.parse(jsonPayload));
};

export const logout = () => {
  localStorage.removeItem('token');
  setToken(null);
  setUser(null);
};

export const isAuthenticated = () => token() !== null;
export const isAdmin = () => user()?.rol === 'ADMINISTRADOR';
export const isVendedor = () => user()?.rol === 'VENDEDOR' || user()?.rol === 'ADMINISTRADOR';

