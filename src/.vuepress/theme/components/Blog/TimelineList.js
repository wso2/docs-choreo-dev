import { __decorate } from "tslib";
import { Component, Mixins } from "vue-property-decorator";
import MyTransition from "@theme/components/MyTransition.vue";
import TimeIcon from "@mr-hope/vuepress-plugin-comment/lib/client/icons/TimeIcon.vue";
import { TimelineMixin } from "@theme/util/articleMixin";
import { getDefaultLocale } from "@mr-hope/vuepress-shared";
let TimelineList = class TimelineList extends Mixins(TimelineMixin) {
    get timeline() {
        return (
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        this.$themeLocaleConfig.blog.timeline || getDefaultLocale().blog.timeline);
    }
    navigate(url) {
        void this.$router.push(url);
    }
};
TimelineList = __decorate([
    Component({ components: { MyTransition, TimeIcon } })
], TimelineList);
export default TimelineList;
//# sourceMappingURL=TimelineList.js.map