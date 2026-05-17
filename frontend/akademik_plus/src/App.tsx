import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import MainPage from './pages/MainPage'
import ProfilePage from './pages/ProfilePage'
import MakePaymentPage from './pages/MakePaymentPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<MainPage />} />
        <Route path="/account" element={<ProfilePage />} />
        <Route path="/account/payment" element={<MakePaymentPage />} />
      </Route>
    </Routes>
  )
}