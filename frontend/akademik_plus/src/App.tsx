import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MainPage from './pages/MainPage';
import ProfilePage from './pages/ProfilePage';
import MakePaymentPage from './pages/MakePaymentPage';
import RequestRepairPage from './pages/RepairPage';
import RoomsPage from './pages/RoomsPage';
import MaintenanceRequestPage from './pages/MaintenanceRequestsPage';
import AdminReportsPage from './pages/AdminReportsPage';

export default function App() {
  return (
    <Routes>
      {/* Public routes */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Protected routes — all wrapped in Layout */}
      <Route element={<Layout />}>
        <Route path="/" element={<ProtectedRoute><MainPage /></ProtectedRoute>} />

        <Route
          path="/account"
          element={<ProtectedRoute><ProfilePage /></ProtectedRoute>}
        />
        <Route
          path="/account/payment"
          element={<ProtectedRoute><MakePaymentPage /></ProtectedRoute>}
        />
        <Route
          path="/account/repair"
          element={<ProtectedRoute><RequestRepairPage /></ProtectedRoute>}
        />

        <Route
          path="/admin/dashboard"
          element={<ProtectedRoute requireAdmin><RoomsPage /></ProtectedRoute>}
        />
        <Route
          path="/admin/dashboard/maintenance_request"
          element={<ProtectedRoute requireAdmin><MaintenanceRequestPage /></ProtectedRoute>}
        />
        <Route
          path="/admin/reports"
          element={<ProtectedRoute requireAdmin><AdminReportsPage /></ProtectedRoute>}
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
