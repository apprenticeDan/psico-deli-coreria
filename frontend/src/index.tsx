/* @refresh reload */
import { render } from 'solid-js/web';
import { Router, Route } from '@solidjs/router';
import './index.css';

import { isAuthenticated } from './store/authStore';
import Login from './pages/Login';
import Layout from './pages/Layout';
import Dashboard from './pages/Dashboard';
import Trabajadores from './pages/Trabajadores';
import Productos from './pages/Productos';

const root = document.getElementById('root');

if (import.meta.env.DEV && !(root instanceof HTMLElement)) {
  throw new Error(
    'Root element not found. Did you forget to add it to your index.html? Or maybe the id attribute got misspelled?',
  );
}

const ProtectedRoute = (props: any) => {
  if (!isAuthenticated()) {
    // Need to do this in a microtask or Solid Router complains about rendering inside render
    setTimeout(() => {
      window.location.href = '/login';
    }, 0);
    return null;
  }
  return <Layout>{props.children}</Layout>;
};

render(() => (
  <Router>
    <Route path="/login" component={Login} />
    <Route path="/" component={() => <ProtectedRoute><Dashboard /></ProtectedRoute>} />
    <Route path="/trabajadores" component={() => <ProtectedRoute><Trabajadores /></ProtectedRoute>} />
    <Route path="/productos" component={() => <ProtectedRoute><Productos /></ProtectedRoute>} />
  </Router>
), root!);
