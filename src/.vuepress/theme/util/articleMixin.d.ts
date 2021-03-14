import { Vue } from "vue-property-decorator";
import type { PageComputed } from "@mr-hope/vuepress-types";
export interface TimelineItem {
    year: number;
    articles: PageComputed[];
}
export declare class TimelineMixin extends Vue {
    protected get $timelineItems(): PageComputed[];
    /** Timeline list */
    protected get $timeline(): TimelineItem[];
}
export declare class StickyMixin extends Vue {
    protected get $stickArticles(): PageComputed[];
}
