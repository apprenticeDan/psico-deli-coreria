import { createSignal } from 'solid-js';
import type { Component } from 'solid-js';
import { useNavigate } from '@solidjs/router';
import { login } from '../store/authStore';
import { authService } from '../services/api';

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
      const data = await authService.login({ usuario: usuario(), password: password() });
      login(data.token);
      navigate('/', { replace: true });
    } catch (err: any) {
      setError(err.message || 'Error de autenticación');
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
