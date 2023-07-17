import { Enums } from "../../commons/enums";
import { AbsComponent } from "../abs-component";
import { ByocConfig } from "../byco-config";
import { WebappConfig } from "./webapp-config";

export interface WebappComponent extends AbsComponent {
    name: string,
    displayName: string,
    description: string,
    projectId: string,
    labels: string,
    componentType: string,
    accessibility: Enums.Accessibility,
    byocWebAppsConfig?:  WebappConfig
}