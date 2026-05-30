import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import MainPage from './pages/MainPage'
import ProfilePage from './pages/ProfilePage'
import MakePaymentPage from './pages/MakePaymentPage'
import RequestRepairPage from './pages/RepairPage'
import RoomsPage from './pages/RoomsPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<MainPage />} />
        <Route path="/account" element={<ProfilePage />} />
        <Route path="/account/payment" element={<MakePaymentPage />} />
        <Route path="/account/repair" element={<RequestRepairPage />} />
        <Route path="/admin/dashboard" element={<RoomsPage />} />
      </Route>
    </Routes>
  )
}