import type { Component } from 'solid-js';
import { user } from '../store/authStore';

const Dashboard: Component = () => {
  return (
    <div class="container">
      <div class="card">
        <h2>Bienvenido, {user()?.nombre}</h2>
        <p>Rol actual: <span class="badge badge-active">{user()?.rol}</span></p>
        <p>Selecciona una opción en el menú superior para comenzar a trabajar.</p>
      </div>
    </div>
  );
};

export default Dashboard;
