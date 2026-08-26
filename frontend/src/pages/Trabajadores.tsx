import { createSignal, createResource, Show } from 'solid-js';
import type { Component } from 'solid-js';
import { token } from '../store/authStore';
import { trabajadoresService } from '../services/api';

const Trabajadores: Component = () => {
  const [trabajadores, { refetch }] = createResource(token, (authToken) =>
    trabajadoresService.getTodos(authToken)
  );
  const [showForm, setShowForm] = createSignal(false);
  const [formData, setFormData] = createSignal({
    nombreCompleto: '',
    usuario: '',
    password: '',
    rol: 'VENDEDOR'
  });
  const [error, setError] = createSignal('');

  const handleSubmit = async (e: Event) => {
    e.preventDefault();
    setError('');
    try {
      await trabajadoresService.crear(formData(), token());
      setShowForm(false);
      setFormData({ nombreCompleto: '', usuario: '', password: '', rol: 'VENDEDOR' });
      refetch();
    } catch (err: any) {
      setError(err.message || 'Error al crear trabajador');
    }
  };

  const handleToggleEstado = async (id: string, estadoActual: string) => {
    const nuevoEstado = estadoActual === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    try {
      await trabajadoresService.cambiarEstado(id, nuevoEstado, token());
      refetch();
    } catch (err: any) {
      alert(err.message || 'Error al cambiar estado');
    }
  };

  const handleCambiarPassword = async (id: string, usuario: string) => {
    const nuevaPassword = prompt(`Ingresa la nueva contraseña para ${usuario}:`);
    if (!nuevaPassword || !nuevaPassword.trim()) return;

    try {
      await trabajadoresService.cambiarPassword(id, nuevaPassword.trim(), token());
      alert(`Contraseña de ${usuario} actualizada con éxito.`);
    } catch (err: any) {
      alert(err.message || 'Error al cambiar contraseña');
    }
  };

  return (
    <div class="container">
      <div style={{ display: 'flex', 'justify-content': 'space-between', 'align-items': 'center', 'margin-bottom': '2rem' }}>
        <h2>Gestión de Trabajadores</h2>
        <button class="btn btn-primary" onClick={() => setShowForm(!showForm())}>
          {showForm() ? 'Cancelar' : 'Nuevo Trabajador'}
        </button>
      </div>

      <Show when={showForm()}>
        <div class="card" style={{ 'margin-bottom': '2rem' }}>
          <h3>Nuevo Trabajador</h3>
          {error() && <div class="alert">{error()}</div>}
          <form onSubmit={handleSubmit}>
            <div class="form-group">
              <label>Nombre Completo</label>
              <input type="text" class="form-control" value={formData().nombreCompleto} onInput={e => setFormData({...formData(), nombreCompleto: e.currentTarget.value})} required />
            </div>
            <div class="form-group">
              <label>Usuario</label>
              <input type="text" class="form-control" value={formData().usuario} onInput={e => setFormData({...formData(), usuario: e.currentTarget.value})} required />
            </div>
            <div class="form-group">
              <label>Contraseña</label>
              <input type="password" class="form-control" value={formData().password} onInput={e => setFormData({...formData(), password: e.currentTarget.value})} required />
            </div>
            <div class="form-group">
              <label>Rol</label>
              <select class="form-control" value={formData().rol} onInput={e => setFormData({...formData(), rol: e.currentTarget.value})}>
                <option value="VENDEDOR">Vendedor</option>
                <option value="ADMINISTRADOR">Administrador</option>
              </select>
            </div>
            <button type="submit" class="btn btn-primary">Guardar</button>
          </form>
        </div>
      </Show>

      <div class="card">
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Nombre</th>
                <th>Usuario</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {trabajadores.loading && <tr><td colspan="6">Cargando...</td></tr>}
              {trabajadores.error && <tr><td colspan="6">Error: {trabajadores.error.message}</td></tr>}
              <Show when={trabajadores()}>
                {trabajadores()?.map((t: any, index: number) => (
                  <tr>
                    <td><strong>#{index + 1}</strong></td>
                    <td>{t.nombreCompleto}</td>
                    <td>{t.usuario}</td>
                    <td>{t.rol}</td>
                    <td>
                      <span class={`badge ${t.estado === 'ACTIVO' ? 'badge-active' : 'badge-inactive'}`}>
                        {t.estado}
                      </span>
                    </td>
                    <td>
                      <button
                        class="btn btn-secondary"
                        style={{ 'margin-right': '0.5rem', padding: '0.25rem 0.5rem', 'font-size': '0.85rem' }}
                        onClick={() => handleCambiarPassword(t.id, t.usuario)}
                      >
                        🔑 Clave
                      </button>
                      <button
                        class={`btn ${t.estado === 'ACTIVO' ? 'btn-danger' : 'btn-primary'}`}
                        style={{ padding: '0.25rem 0.5rem', 'font-size': '0.85rem' }}
                        onClick={() => handleToggleEstado(t.id, t.estado)}
                      >
                        {t.estado === 'ACTIVO' ? 'Inactivar' : 'Activar'}
                      </button>
                    </td>
                  </tr>
                ))}
              </Show>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Trabajadores;
