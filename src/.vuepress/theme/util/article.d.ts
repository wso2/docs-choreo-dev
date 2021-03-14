import type { PageComputed } from "@mr-hope/vuepress-types";
export declare const getDate: (date: string | Date) => (number | undefined)[];
export declare const compareDate: (dataA: Date | string | undefined, dataB: Date | string | undefined) => number;
export declare const filterArticle: (pages: PageComputed[], filterFunc?: ((page: PageComputed) => boolean) | undefined) => PageComputed[];
export declare const sortArticle: (pages: PageComputed[]) => PageComputed[];
export declare const generatePagination: (pages: PageComputed[], perPage?: number) => PageComputed[][];
