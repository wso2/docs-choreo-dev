import { __decorate } from "tslib";
import { Component, Vue } from "vue-property-decorator";
import { filterArticle, getDate, sortArticle } from "./article";
let TimelineMixin = class TimelineMixin extends Vue {
    get $timelineItems() {
        const { pages } = this.$site;
        // filter before sort
        return sortArticle(filterArticle(pages, (page) => Boolean(page.frontmatter.time || page.frontmatter.date) &&
            page.frontmatter.timeline !== false));
    }
    /** Timeline list */
    get $timeline() {
        const timelineItems = [];
        // filter before sort
        this.$timelineItems.forEach((article) => {
            const { frontmatter: { date, time = date }, } = article;
            const [year, month, day] = getDate(time);
            if (year && month && day) {
                if (!timelineItems[0] || timelineItems[0].year !== year)
                    timelineItems.unshift({ year, articles: [] });
                article.frontmatter.parsedDate = `${month}-${day}`;
                timelineItems[0].articles.push(article);
            }
        });
        return timelineItems.reverse();
    }
};
TimelineMixin = __decorate([
    Component
], TimelineMixin);
export { TimelineMixin };
let StickyMixin = class StickyMixin extends Vue {
    get $stickArticles() {
        const { pages } = this.$site;
        // filter before sort
        return sortArticle(filterArticle(pages, (page) => Boolean(page.frontmatter.sticky)));
    }
};
StickyMixin = __decorate([
    Component
], StickyMixin);
export { StickyMixin };
//# sourceMappingURL=articleMixin.js.map