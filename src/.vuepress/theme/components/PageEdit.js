import Vue from "vue";
import { endingSlashRE, outboundRE } from "@theme/util/path";
export default Vue.extend({
    name: "PageEdit",
    computed: {
        lastUpdated() {
            return this.$themeConfig.lastUpdate === false
                ? ""
                : this.$page.lastUpdated || "";
        },
        lastUpdatedText() {
            return this.$themeLocaleConfig.lastUpdated || "Last Updated";
        },
        editLink() {
            const showEditLink = this.$page.frontmatter.editLink ||
                (this.$themeConfig.editLinks !== false &&
                    this.$page.frontmatter.editLink !== false);
            const { repo, docsRepo } = this.$site.themeConfig;
            if (showEditLink && (repo || docsRepo) && this.$page.relativePath)
                return this.createEditLink();
            return false;
        },
        editLinkText() {
            return this.$themeLocaleConfig.editLinkText || "Edit this page";
        },
    },
    methods: {
        createEditLink() {
            const { repo = "", docsRepo = repo, docsDir = "", docsBranch = "master", } = this.$themeConfig;
            const bitbucket = /bitbucket.org/u;
            if (bitbucket.test(docsRepo))
                return `${docsRepo.replace(endingSlashRE, "")}/src/${docsBranch}/${docsDir ? `${docsDir.replace(endingSlashRE, "")}/` : ""}${this.$page.relativePath}?mode=edit&spa=0&at=${docsBranch}&fileviewer=file-view-default`;
            const gitlab = /gitlab.com/u;
            if (gitlab.test(docsRepo))
                return `${docsRepo.replace(endingSlashRE, "")}/-/edit/${docsBranch}/${docsDir ? `${docsDir.replace(endingSlashRE, "")}/` : ""}${this.$page.relativePath}`;
            const base = outboundRE.test(docsRepo)
                ? docsRepo
                : `https://github.com/${docsRepo}`;
            return `${base.replace(endingSlashRE, "")}/edit/${docsBranch}/${docsDir ? `${docsDir.replace(endingSlashRE, "")}/` : ""}${this.$page.relativePath}`;
        },
    },
});
//# sourceMappingURL=PageEdit.js.map