/**
 * Base da API REST (`/api/v1` ou URL absoluta do backend, ex. Render).
 * Em dev, com valor vazio, o Vite encaminha `/api` para `VITE_DEV_PROXY_TARGET` (vite.config).
 */
export function getApiBaseUrl(): string {
  const raw = import.meta.env.VITE_API_BASE_URL as string | undefined;
  if (raw != null && raw.trim() !== "") {
    return raw.trim().replace(/\/+$/, "");
  }
  return "/api/v1";
}
