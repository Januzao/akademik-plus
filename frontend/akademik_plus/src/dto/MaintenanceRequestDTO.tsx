export interface MaintenanceRequestReqDTO {
  id?: number;
  roomId?: number;
  roomNumber?: string;
  category?: string;
  priority?: "High" | "Medium" | "Low";
  status?: "Pending" | "In Progress" | "Completed";
  description?: string;
  requestDate?: string;
  photoUrl?: string;
  tenantName?: string;
  tenantPhone?: string;
}

export interface MaintenanceRequestRespDTO {
  id?: number;
  category?: string;
  priority?: "High" | "Medium" | "Low" | string;
  status?: "Pending" | "In Progress" | "Completed" | string;
  description?: string;
  requestDate?: string;
  photoUrl?: string;
  roomNumber?: string;
  tenantName?: string;
  tenantPhone?: string;
}