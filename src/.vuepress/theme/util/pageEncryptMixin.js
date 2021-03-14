import { __decorate } from "tslib";
import { compareSync } from "bcryptjs";
import { Component, Vue } from "vue-property-decorator";
import { getPathMatchedKeys } from "./encrypt";
let PageEncryptMixin = class PageEncryptMixin extends Vue {
    constructor() {
        super(...arguments);
        this.encryptConfig = {};
    }
    get encryptOptions() {
        return this.$themeConfig.encrypt || {};
    }
    get pathMatchKeys() {
        return getPathMatchedKeys(this.encryptConfig, this.$route.path);
    }
    get isPathEncrypted() {
        if (this.pathMatchKeys.length !== 0) {
            const { config } = this.encryptOptions;
            // none of the password matches
            return !this.pathMatchKeys.some((key) => {
                const keyConfig = config[key];
                const hitPasswords = typeof keyConfig === "string" ? [keyConfig] : keyConfig;
                return hitPasswords.some((encryptPassword) => compareSync(this.encryptConfig[key], encryptPassword));
            });
        }
        return false;
    }
    setPassword(password) {
        const { config } = this.$themeConfig.encrypt;
        for (const hitKey of this.pathMatchKeys) {
            const hitPassword = config[hitKey];
            const hitPasswordList = typeof hitPassword === "string" ? [hitPassword] : hitPassword;
            // some of the password matches
            if (hitPasswordList.filter((encryptPassword) => compareSync(password, encryptPassword))) {
                this.$set(this.encryptConfig, hitKey, password);
                localStorage.setItem("encryptConfig", JSON.stringify(this.encryptConfig));
                break;
            }
        }
    }
    mounted() {
        const passwordConfig = localStorage.getItem("encryptConfig");
        if (passwordConfig)
            this.encryptConfig = JSON.parse(passwordConfig);
    }
};
PageEncryptMixin = __decorate([
    Component
], PageEncryptMixin);
export default PageEncryptMixin;
//# sourceMappingURL=pageEncryptMixin.js.map