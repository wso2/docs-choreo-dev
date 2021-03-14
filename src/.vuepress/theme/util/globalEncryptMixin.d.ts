import { Vue } from "vue-property-decorator";
import type { EncryptOptions } from "../types";
export default class GlobalEncryptMixin extends Vue {
    protected globalPassword: string;
    protected get encryptOptions(): EncryptOptions;
    protected get isGlobalEncrypted(): boolean;
    protected mounted(): void;
    protected checkGlobalPassword(globalPassword: string): void;
}
