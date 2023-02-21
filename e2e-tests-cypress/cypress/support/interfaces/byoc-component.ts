import { Enums } from "../console/enums";
import { AbsComponent } from "./abs-component";
import { ByocConfig } from "./byco-config";

export interface ByocComponent extends AbsComponent {
    name: string,
    displayName: string,
    description: string,
    projectId: string,
    labels: string,
    componentType: string,
    port?: 80,
    oasFilePath: string,
    accessibility: Enums.Accessibility,
    byocConfig?: ByocConfig
}