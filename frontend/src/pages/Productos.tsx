import { createSignal, createResource, Show } from 'solid-js';
import type { Component } from 'solid-js';
import { token } from '../store/authStore';
import { productosService } from '../services/api';
import type { CrearProductoDto } from '../services/api';

const Productos: Component = () => {
  const [productos, { refetch }] = createResource(token, (authToken) =>
    productosService.getTodos(authToken)
  );

  const [showForm, setShowForm] = createSignal(false);
  const [formData, setFormData] = createSignal<CrearProductoDto>({
    codigo: '',
    nombre: '',
    marca: '',
    categoria: 'CERVEZA',
    empaque: 'Botella',
    contenido: 620,
    unidad: 'ML',
    precio: 15.0,
    stock: 20,
    esCigarrillo: false,
    unidadesPorCajetilla: 20,
    precioUnidadSuelta: 1.5,
  });

  const [error, setError] = createSignal('');

  const prefijoCategoria = (cat: string) => {
    switch (cat) {
      case 'CERVEZA': return 'CER';
      case 'GASEOSA': return 'GAS';
      case 'CIGARRILLO': return 'CIG';
      case 'REFRESCO': return 'REF';
      case 'TRAGO': return 'TRA';
      case 'COMBO': return 'COM';
      default: return 'PROD';
    }
  };

  const handleCategoriaChange = (cat: string) => {
    const isCig = cat === 'CIGARRILLO';
    setFormData({
      ...formData(),
      categoria: cat,
      esCigarrillo: isCig,
      empaque: isCig ? 'Cajetilla' : formData().empaque,
      unidad: isCig ? 'UNIDAD' : formData().unidad,
    });
  };

  const handleSubmit = async (e: Event) => {
    e.preventDefault();
    setError('');
    try {
      const dataToSend = {
        ...formData(),
        codigo: undefined, // El backend genera el código correlativo según la categoría
      };
      await productosService.crear(dataToSend, token());
      setShowForm(false);
      setFormData({
        codigo: '',
        nombre: '',
        marca: '',
        categoria: 'CERVEZA',
        empaque: 'Botella',
        contenido: 620,
        unidad: 'ML',
        precio: 15.0,
        stock: 20,
        esCigarrillo: false,
        unidadesPorCajetilla: 20,
        precioUnidadSuelta: 1.5,
      });
      refetch();
    } catch (err: any) {
      setError(err.message || 'Error al guardar el producto');
    }
  };

  const handleToggleEstado = async (id: string, estadoActual: string) => {
    const nuevoEstado = estadoActual === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    try {
      await productosService.cambiarEstado(id, nuevoEstado, token());
      refetch();
    } catch (err: any) {
      alert(err.message || 'Error al cambiar estado');
    }
  };

  const handleAbrirCajetilla = async (id: string, nombre: string) => {
    const cantidadStr = prompt(`¿Cuántas cajetillas de ${nombre} deseas abrir?`, '1');
    if (!cantidadStr) return;
    const cantidad = parseInt(cantidadStr, 10);
    if (isNaN(cantidad) || cantidad <= 0) {
      alert('Ingresa una cantidad válida.');
      return;
    }

    try {
      await productosService.abrirCajetilla(id, cantidad, token());
      alert(`Se abrió ${cantidad} cajetilla(s) con éxito.`);
      refetch();
    } catch (err: any) {
      alert(err.message || 'Error al abrir cajetilla');
    }
  };

  return (
    <div class="container">
      <div style={{ display: 'flex', 'justify-content': 'space-between', 'align-items': 'center', 'margin-bottom': '2rem' }}>
        <h2>Catálogo de Productos y Cigarrillos</h2>
        <button class="btn btn-primary" onClick={() => setShowForm(!showForm())}>
          {showForm() ? 'Cancelar' : 'Nuevo Producto'}
        </button>
      </div>

      <Show when={showForm()}>
        <div class="card" style={{ 'margin-bottom': '2rem' }}>
          <h3>Nuevo Producto</h3>
          {error() && <div class="alert">{error()}</div>}
          <form onSubmit={handleSubmit}>
            <div style={{ display: 'grid', 'grid-template-columns': '1fr 1fr', gap: '1rem' }}>
              <div class="form-group">
                <label>Código de Producto</label>
                <input
                  type="text"
                  class="form-control"
                  style={{ background: '#f1f5f9', cursor: 'not-allowed', color: '#64748b' }}
                  value={`Autogenerado (${prefijoCategoria(formData().categoria)}-XXXX)`}
                  disabled
                  readOnly
                />
                <small style={{ color: '#64748b', 'font-size': '0.8rem' }}>
                  El sistema asignará automáticamente el correlativo según la categoría.
                </small>
              </div>

              <div class="form-group">
                <label>Nombre del Producto</label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Ej: Paceña Centenario"
                  value={formData().nombre}
                  onInput={(e) => setFormData({ ...formData(), nombre: e.currentTarget.value })}
                  required
                />
              </div>

              <div class="form-group">
                <label>Marca</label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Ej: CBN / Camel"
                  value={formData().marca || ''}
                  onInput={(e) => setFormData({ ...formData(), marca: e.currentTarget.value })}
                />
              </div>

              <div class="form-group">
                <label>Categoría</label>
                <select
                  class="form-control"
                  value={formData().categoria}
                  onInput={(e) => handleCategoriaChange(e.currentTarget.value)}
                >
                  <option value="CERVEZA">Cerveza</option>
                  <option value="GASEOSA">Gaseosa</option>
                  <option value="CIGARRILLO">Cigarrillo</option>
                  <option value="REFRESCO">Refresco</option>
                  <option value="TRAGO">Trago / Licor</option>
                  <option value="COMBO">Combo</option>
                </select>
              </div>

              <div class="form-group">
                <label>Empaque (Envase)</label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Ej: Botella / Cajetilla"
                  value={formData().empaque || ''}
                  onInput={(e) => setFormData({ ...formData(), empaque: e.currentTarget.value })}
                />
              </div>

              <div class="form-group">
                <label>Contenido Neto</label>
                <input
                  type="number"
                  step="0.01"
                  class="form-control"
                  value={formData().contenido || 1}
                  onInput={(e) => setFormData({ ...formData(), contenido: parseFloat(e.currentTarget.value) })}
                />
              </div>

              <div class="form-group">
                <label>Unidad de Medida</label>
                <select
                  class="form-control"
                  value={formData().unidad || 'ML'}
                  onInput={(e) => setFormData({ ...formData(), unidad: e.currentTarget.value })}
                >
                  <option value="ML">ML (Mililitros)</option>
                  <option value="L">L (Litros)</option>
                  <option value="UNIDAD">Unidades</option>
                  <option value="KG">KG (Kilogramos)</option>
                  <option value="CAJETILLA">Cajetilla</option>
                </select>
              </div>

              <div class="form-group">
                <label>Precio Venta (Bs.)</label>
                <input
                  type="number"
                  step="0.10"
                  class="form-control"
                  value={formData().precio}
                  onInput={(e) => setFormData({ ...formData(), precio: parseFloat(e.currentTarget.value) })}
                  required
                />
              </div>

              <div class="form-group">
                <label>Stock Inicial (Unidades/Cajetillas)</label>
                <input
                  type="number"
                  class="form-control"
                  value={formData().stock || 0}
                  onInput={(e) => setFormData({ ...formData(), stock: parseInt(e.currentTarget.value, 10) })}
                />
              </div>
            </div>

            <Show when={formData().categoria === 'CIGARRILLO'}>
              <div class="card" style={{ background: '#f8fafc', 'margin-top': '1rem', padding: '1rem' }}>
                <h4>Configuración Especial para Cigarrillos</h4>
                <div style={{ display: 'grid', 'grid-template-columns': '1fr 1fr', gap: '1rem' }}>
                  <div class="form-group">
                    <label>Unidades por Cajetilla</label>
                    <input
                      type="number"
                      class="form-control"
                      value={formData().unidadesPorCajetilla || 20}
                      onInput={(e) => setFormData({ ...formData(), unidadesPorCajetilla: parseInt(e.currentTarget.value, 10) })}
                    />
                  </div>
                  <div class="form-group">
                    <label>Precio Unidad Suelta (Bs.)</label>
                    <input
                      type="number"
                      step="0.10"
                      class="form-control"
                      value={formData().precioUnidadSuelta || 1.5}
                      onInput={(e) => setFormData({ ...formData(), precioUnidadSuelta: parseFloat(e.currentTarget.value) })}
                    />
                  </div>
                </div>
              </div>
            </Show>

            <div style={{ 'margin-top': '1.5rem' }}>
              <button type="submit" class="btn btn-primary">Guardar Producto</button>
            </div>
          </form>
        </div>
      </Show>

      <div class="card">
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Código</th>
                <th>Producto / Marca</th>
                <th>Categoría</th>
                <th>Presentación</th>
                <th>Precio Base</th>
                <th>Stock Existente</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {productos.loading && <tr><td colspan="9">Cargando catálogo...</td></tr>}
              {productos.error && <tr><td colspan="9">Error: {productos.error.message}</td></tr>}
              <Show when={productos()}>
                {productos()?.map((p: any, index: number) => (
                  <tr>
                    <td><strong>#{index + 1}</strong></td>
                    <td><code>{p.codigo}</code></td>
                    <td>
                      <strong>{p.nombre}</strong>
                      {p.marca && <div style={{ 'font-size': '0.8rem', color: '#64748b' }}>{p.marca}</div>}
                    </td>
                    <td><span class="badge" style={{ background: '#e2e8f0', color: '#334155' }}>{p.categoria}</span></td>
                    <td>{p.presentacionTexto}</td>
                    <td>Bs. {p.precio?.toFixed(2)}</td>
                    <td>
                      <div><strong>{p.stock}</strong> {p.esCigarrillo ? 'cajetillas' : 'unidades'}</div>
                      {p.esCigarrillo && (
                        <div style={{ 'font-size': '0.8rem', color: '#0284c7' }}>
                          Sueltas: {p.stockUnidadesSueltas || 0} u
                        </div>
                      )}
                    </td>
                    <td>
                      <span class={`badge ${p.estado === 'ACTIVO' ? 'badge-active' : 'badge-inactive'}`}>
                        {p.estado}
                      </span>
                    </td>
                    <td>
                      <Show when={p.esCigarrillo}>
                        <button
                          class="btn btn-secondary"
                          style={{ 'margin-right': '0.5rem', padding: '0.25rem 0.5rem', 'font-size': '0.85rem' }}
                          onClick={() => handleAbrirCajetilla(p.id, p.nombre)}
                        >
                          📦 Abrir Cajetilla
                        </button>
                      </Show>
                      <button
                        class={`btn ${p.estado === 'ACTIVO' ? 'btn-danger' : 'btn-primary'}`}
                        style={{ padding: '0.25rem 0.5rem', 'font-size': '0.85rem' }}
                        onClick={() => handleToggleEstado(p.id, p.estado)}
                      >
                        {p.estado === 'ACTIVO' ? 'Inactivar' : 'Activar'}
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

export default Productos;
