package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.MerkelTree
import com.sword.signature.common.criteria.JobCriteria
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface JobService {

    /**
     * Retrieve all jobs matching the criteria.
     * @param requester The account requesting the jobs.
     * @param criteria Search criteria for job search.
     * @param pageable Pageable for memory-optimized job retrieval.
     * @return Jobs matching the criteria.
     */
    fun findAll(requester: Account, criteria: JobCriteria? = null, pageable: Pageable = Pageable.unpaged()): Flow<Job>

    /**
     * Count the number of jobs matching the criteria.
     * @param requester The account requesting the jobs.
     * @param criteria Search criteria for job search.
     * @return Number of jobs matching the criteria.
     */
    fun countAll(requester: Account, criteria: JobCriteria? = null): Long

    /**
     * Retrieve a job by its id.
     * @param requester The account requesting the job.
     * @param jobId Id of the job to retrieve.
     * @param withLeaves Include or not the job merkle tree leaves in the returned object.
     * @return The job if it exists, null otherwise.
     */
    suspend fun findById(requester: Account, jobId: String, withLeaves: Boolean = false): Job?

    /**
     * Retrieve the merkel tree of a Job
     * @param requester The account requesting the job.
     * @param jobId Id of the job to retrieve.
     * @return The MerkelTree if it exists, null otherwise.
     */
    suspend fun getMerkelTree(requester: Account, jobId: String): MerkelTree?

    /**
     * Update a job with provided fields.
     * @param requester The account requesting the job.
     * @param jobId Id of the job to update.
     * @param patch Fields to update.
     * @return The updated job.
     */
    suspend fun patch(requester: Account, jobId: String, patch: JobPatch): Job



}
