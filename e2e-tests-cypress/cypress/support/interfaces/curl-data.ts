import { Header } from "./header";

export interface CurlData {
    method: string,
    url: string,
    headers: Header
}