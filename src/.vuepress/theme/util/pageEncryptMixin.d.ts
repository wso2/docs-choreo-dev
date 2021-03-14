import { Vue } from "vue-property-decorator";
import type { EncryptOptions } from "../types";
export default class PageEncryptMixin extends Vue {
    protected encryptConfig: Record<string, string>;
    protected get encryptOptions(): EncryptOptions;
    protected get pathMatchKeys(): string[];
    protected get isPathEncrypted(): boolean;
    protected setPassword(password: string): void;
    protected mounted(): void;
}
