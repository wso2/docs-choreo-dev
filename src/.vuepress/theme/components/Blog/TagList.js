import Vue from "vue";
import { getDefaultLocale } from "@mr-hope/vuepress-shared";
import { navigate } from "@theme/util/navigate";
export default Vue.extend({
    name: "TagList",
    computed: {
        tagList() {
            return [
                {
                    name: 
                    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                    this.$themeLocaleConfig.blog.allText ||
                        getDefaultLocale().blog.allText,
                    path: "/tag/",
                },
                ...this.$tag.list,
            ];
        },
    },
    methods: {
        isActive(name) {
            return (name ===
                ((this.$currentTag && this.$currentTag.key) ||
                    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                    this.$themeLocaleConfig.blog.allText ||
                    getDefaultLocale().blog.allText));
        },
        clickTag(path) {
            navigate(path, this.$router, this.$route);
        },
    },
});
//# sourceMappingURL=TagList.js.map