import { __decorate } from "tslib";
import { compareSync } from "bcryptjs";
import { Component, Vue } from "vue-property-decorator";
let GlobalEncryptMixin = class GlobalEncryptMixin extends Vue {
    constructor() {
        super(...arguments);
        this.globalPassword = "";
    }
    get encryptOptions() {
        return this.$themeConfig.encrypt || {};
    }
    get isGlobalEncrypted() {
        if (this.encryptOptions.status === "global" && this.encryptOptions.global) {
            const { global } = this.encryptOptions;
            const globalPasswords = typeof global === "string" ? [global] : global;
            // none of the password matches
            return !globalPasswords.some((globalPassword) => compareSync(this.globalPassword, globalPassword));
        }
        return false;
    }
    mounted() {
        const globalPassword = localStorage.getItem("globalPassword");
        if (globalPassword)
            this.globalPassword = globalPassword;
    }
    checkGlobalPassword(globalPassword) {
        const { global } = this.encryptOptions;
        const globalPasswords = typeof global === "string" ? [global] : global;
        if (
        // some of the password matches
        globalPasswords.some((password) => compareSync(globalPassword, password))) {
            this.globalPassword = globalPassword;
            localStorage.setItem("globalPassword", globalPassword);
        }
    }
};
GlobalEncryptMixin = __decorate([
    Component
], GlobalEncryptMixin);
export default GlobalEncryptMixin;
//# sourceMappingURL=globalEncryptMixin.js.map