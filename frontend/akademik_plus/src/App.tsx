import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import MainPage from './pages/MainPage'
import ProfilePage from './pages/ProfilePage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<MainPage />} />
        <Route path="/account" element={<ProfilePage />} />
      </Route>
    </Routes>
  )
}