/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.Company;

/**
 * Service class for various operations to be performed for a company
 *
 * @author : Shital Raval
 * Date : 30/7/19
 * Time : 2:10 PM
 * Class Name : ICompanyService
 * Project Name : server
 */
public interface ICompanyService {
    /**
     * Service method to add / update a company
     *
     * @param company the company to be added
     * @throws Exception
     */
    void saveCompany(Company company) throws Exception;
}
