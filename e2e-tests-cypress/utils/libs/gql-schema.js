module.exports = {
    buildGetProjectsQuery: function(orgId) {
        return JSON.stringify({
            query: `query{projects(orgId: ${orgId}){      id, orgId, name, version, createdDate, handler, region, description    }}`
          });
    },

    buildGetComponentsQuery: function(orgHandler, projectId) {
        return JSON.stringify({ query: `query{ components(orgHandler:  "${orgHandler}", projectId: "${projectId}" )` +
        `{\n projectId, \n id, \n description, \n name, \n handler,` +
            `\n displayName, \n displayType, \n version, \n createdAt,` +
            `\n lastBuildDate,\n orgHandler, \n apiVersions { \n apiVersion,` +
            `\n proxyName,\n proxyUrl,\n proxyId,\n id,\n state,\n latest,` +
            `\n branch,\n accessibility\n }\n } \n }` });
    },
};