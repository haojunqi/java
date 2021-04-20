package com.offcn.project.service;

import com.offcn.project.vo.req.ProjectToReidsVo;

public interface ProjectCreatservice {

    public String creatProject(Integer memberId);

    public void saveProjectInfo(ProjectToReidsVo reidsVo);
}
