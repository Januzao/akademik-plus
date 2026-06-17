export interface MaintenanceRequestReqDTO {
  id?: number;
  roomId?: number;
  roomNumber?: string;
  category?: string;
  priority?: "High" | "Medium" | "Low";
  status?: "PENDING" | "IN_PROGRESS" | "RESOLVED";
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
  status?: "PENDING" | "IN_PROGRESS" | "RESOLVED" | string;
  description?: string;
  requestDate?: string;
  photoUrl?: string;
  roomNumber?: string;
  tenantName?: string;
  tenantPhone?: string;
}