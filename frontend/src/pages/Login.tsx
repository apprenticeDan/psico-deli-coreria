import { createSignal } from 'solid-js';
import type { Component } from 'solid-js';
import { useNavigate } from '@solidjs/router';
import { login } from '../store/authStore';

const Login: Component = () => {
  const [usuario, setUsuario] = createSignal('');
  const [password, setPassword] = createSignal('');
  const [error, setError] = createSignal('');
  const [loading, setLoading] = createSignal(false);
  const navigate = useNavigate();

  const handleLogin = async (e: Event) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ usuario: usuario(), password: password() }),
      });

      if (res.ok) {
        const data = await res.json();
        login(data.token);
        navigate('/', { replace: true });
      } else {
        const errorText = await res.text();
        setError(errorText || 'Error de autenticación');
      }
    } catch (err) {
      setError('No se pudo conectar al servidor');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', 'justify-content': 'center', 'align-items': 'center', height: '100vh' }}>
      <div class="card" style={{ width: '400px' }}>
        <h2 style={{ 'text-align': 'center', 'margin-bottom': '2rem' }}>Psico-Deli Corería</h2>
        {error() && <div class="alert">{error()}</div>}
        <form onSubmit={handleLogin}>
          <div class="form-group">
            <label>Usuario</label>
            <input
              type="text"
              class="form-control"
              value={usuario()}
              onInput={(e) => setUsuario(e.currentTarget.value)}
              required
            />
          </div>
          <div class="form-group">
            <label>Contraseña</label>
            <input
              type="password"
              class="form-control"
              value={password()}
              onInput={(e) => setPassword(e.currentTarget.value)}
              required
            />
          </div>
          <button type="submit" class="btn btn-primary" style={{ width: '100%' }} disabled={loading()}>
            {loading() ? 'Iniciando...' : 'Iniciar Sesión'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;
