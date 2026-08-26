const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8088/api';

export interface RequestOptions extends Omit<RequestInit, 'body'> {
  params?: Record<string, string | number | boolean | undefined | null>;
  body?: any;
  token?: string | null;
}

export class ApiError extends Error {
  public status: number;
  public data?: any;

  constructor(message: string, status: number, data?: any) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

/**
 * Cliente HTTP base centralizado y extensible para peticiones al backend.
 */
export const apiClient = {
  async request<T>(endpoint: string, options: RequestOptions = {}): Promise<T> {
    const { params, body, token, headers: customHeaders, ...customOptions } = options;

    // Construcción de la URL con query parameters si existen
    let url = `${BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;
    if (params) {
      const searchParams = new URLSearchParams();
      Object.entries(params).forEach(([key, val]) => {
        if (val !== undefined && val !== null) {
          searchParams.append(key, String(val));
        }
      });
      const queryString = searchParams.toString();
      if (queryString) {
        url += (url.includes('?') ? '&' : '?') + queryString;
      }
    }

    // Cabeceras por defecto
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...((customHeaders as Record<string, string>) || {}),
    };

    // Inyección de Token JWT si se provee
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    // Preparación del body
    let requestBody: BodyInit | null = null;
    if (body !== undefined && body !== null) {
      if (typeof body === 'string' || body instanceof FormData || body instanceof URLSearchParams) {
        requestBody = body;
      } else {
        requestBody = JSON.stringify(body);
      }
    }

    const response = await fetch(url, {
      ...customOptions,
      headers,
      body: requestBody,
    });

    if (!response.ok) {
      let errorData: any;
      let errorText = '';
      try {
        errorText = await response.text();
        errorData = JSON.parse(errorText);
      } catch {
        // En caso de que la respuesta de error no sea JSON
      }
      const message = errorData?.message || errorData?.error || errorText || `Error HTTP ${response.status}`;
      throw new ApiError(message, response.status, errorData);
    }

    // Si no hay contenido (204 No Content)
    if (response.status === 204) {
      return {} as T;
    }

    return response.json();
  },

  get<T>(endpoint: string, options?: Omit<RequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, { ...options, method: 'GET' });
  },

  post<T>(endpoint: string, body?: any, options?: Omit<RequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, { ...options, method: 'POST', body });
  },

  put<T>(endpoint: string, body?: any, options?: Omit<RequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, { ...options, method: 'PUT', body });
  },

  patch<T>(endpoint: string, body?: any, options?: Omit<RequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, { ...options, method: 'PATCH', body });
  },

  delete<T>(endpoint: string, options?: Omit<RequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, { ...options, method: 'DELETE' });
  },
};

// --- Tipos e Interfaces de Dominio ---

export interface LoginCredentials {
  usuario: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface TrabajadorDto {
  id?: string | number;
  nombreCompleto: string;
  usuario: string;
  rol: 'ADMINISTRADOR' | 'VENDEDOR';
  estado?: 'ACTIVO' | 'INACTIVO';
}

export interface CrearTrabajadorDto {
  nombreCompleto: string;
  usuario: string;
  password: string;
  rol: string;
}

// --- Servicios de Dominio Reutilizables ---

export const authService = {
  login: (credentials: LoginCredentials): Promise<LoginResponse> =>
    apiClient.post<LoginResponse>('/auth/login', credentials),
};

export const trabajadoresService = {
  getTodos: (authToken?: string | null): Promise<TrabajadorDto[]> =>
    apiClient.get<TrabajadorDto[]>('/trabajadores', { token: authToken }),

  crear: (data: CrearTrabajadorDto, authToken?: string | null): Promise<TrabajadorDto> =>
    apiClient.post<TrabajadorDto>('/trabajadores', data, { token: authToken }),
};
