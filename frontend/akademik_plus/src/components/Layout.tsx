import { Outlet } from 'react-router-dom'
import Topbar from './Topbar'

export default function Layout() {
  return (
    <div className="min-h-screen bg-gray-100">
      <Topbar />
      <main>
        <Outlet />
      </main>
    </div>
  )
}