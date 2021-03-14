import { __decorate } from "tslib";
import { Component, Mixins } from "vue-property-decorator";
import { getDefaultLocale } from "@mr-hope/vuepress-shared";
import ArticleIcon from "@theme/icons/ArticleIcon.vue";
import CategoryIcon from "@mr-hope/vuepress-plugin-comment/lib/client/icons/CategoryIcon.vue";
import TagIcon from "@mr-hope/vuepress-plugin-comment/lib/client/icons/TagIcon.vue";
import TimeIcon from "@mr-hope/vuepress-plugin-comment/lib/client/icons/TimeIcon.vue";
import ArticleList from "@theme/components/Blog/ArticleList.vue";
import CategoryList from "@theme/components/Blog/CategoryList.vue";
import MyTransition from "@theme/components/MyTransition.vue";
import TagList from "@theme/components/Blog/TagList.vue";
import Timeline from "@theme/components/Blog/Timeline.vue";
import TimelineList from "@theme/components/Blog/TimelineList.vue";
import { filterArticle } from "@theme/util/article";
import { StickyMixin } from "@theme/util/articleMixin";
let BlogInfo = class BlogInfo extends Mixins(StickyMixin) {
    constructor() {
        super(...arguments);
        this.active = "category";
    }
    get i18n() {
        return this.$themeLocaleConfig.blog || getDefaultLocale().blog;
    }
    get articleNumber() {
        return filterArticle(this.$site.pages).length;
    }
    setActive(name) {
        this.active = name;
    }
};
BlogInfo = __decorate([
    Component({
        components: {
            ArticleIcon,
            ArticleList,
            CategoryIcon,
            CategoryList,
            MyTransition,
            TagIcon,
            TagList,
            TimeIcon,
            Timeline,
            TimelineList,
        },
    })
], BlogInfo);
export default BlogInfo;
//# sourceMappingURL=BlogInfoList.js.map