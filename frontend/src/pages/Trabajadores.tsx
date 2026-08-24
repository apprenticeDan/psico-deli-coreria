import { Component, createSignal, createResource, Show } from 'solid-js';
import { token } from '../store/authStore';

const fetchTrabajadores = async () => {
  const res = await fetch('http://localhost:8080/api/trabajadores', {
    headers: { 'Authorization': `Bearer ${token()}` }
  });
  if (!res.ok) throw new Error('Error cargando trabajadores');
  return res.json();
};

const Trabajadores: Component = () => {
  const [trabajadores, { refetch }] = createResource(fetchTrabajadores);
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
      const res = await fetch('http://localhost:8080/api/trabajadores', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token()}`
        },
        body: JSON.stringify(formData())
      });

      if (res.ok) {
        setShowForm(false);
        setFormData({ nombreCompleto: '', usuario: '', password: '', rol: 'VENDEDOR' });
        refetch();
      } else {
        const err = await res.text();
        setError(err);
      }
    } catch (err) {
      setError('Error al crear trabajador');
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
                <th>Nombre</th>
                <th>Usuario</th>
                <th>Rol</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {trabajadores.loading && <tr><td colspan="4">Cargando...</td></tr>}
              {trabajadores.error && <tr><td colspan="4">Error: {trabajadores.error.message}</td></tr>}
              <Show when={trabajadores()}>
                {trabajadores()?.map((t: any) => (
                  <tr>
                    <td>{t.nombreCompleto}</td>
                    <td>{t.usuario}</td>
                    <td>{t.rol}</td>
                    <td>
                      <span class={`badge ${t.estado === 'ACTIVO' ? 'badge-active' : 'badge-inactive'}`}>
                        {t.estado}
                      </span>
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
