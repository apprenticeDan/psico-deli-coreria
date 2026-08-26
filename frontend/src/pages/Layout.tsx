import type { Component } from 'solid-js';
import { A, useNavigate } from '@solidjs/router';
import { logout, isAdmin } from '../store/authStore';

const Layout: Component<any> = (props) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
      <nav class="nav-bar">
        <div style={{ 'font-weight': 'bold', 'font-size': '1.2rem' }}>Psico-Deli</div>
        <div class="nav-links">
          <A href="/" class="nav-link" activeClass="active" end>Dashboard</A>
          <A href="/productos" class="nav-link" activeClass="active">Productos</A>
          {isAdmin() && <A href="/trabajadores" class="nav-link" activeClass="active">Trabajadores</A>}
        </div>
        <button class="btn btn-secondary" onClick={handleLogout}>Cerrar Sesión</button>
      </nav>
      <main>
        {props.children}
      </main>
    </>
  );
};

export default Layout;
