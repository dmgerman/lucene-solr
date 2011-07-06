begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentLinkedQueue
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An TemporaryObjectAllocator is an object which manages large, reusable,  * temporary objects needed during multiple concurrent computations. The idea  * is to remember some of the previously allocated temporary objects, and  * reuse them if possible to avoid constant allocation and garbage-collection  * of these objects.   *<P>  * This technique is useful for temporary counter arrays in faceted search  * (see {@link FacetsAccumulator}), which can be reused across searches instead  * of being allocated afresh on every search.  *<P>  * A TemporaryObjectAllocator is thread-safe.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|TemporaryObjectAllocator
specifier|public
specifier|abstract
class|class
name|TemporaryObjectAllocator
parameter_list|<
name|T
parameter_list|>
block|{
comment|// In the "pool" we hold up to "maxObjects" old objects, and if the pool
comment|// is not empty, we return one of its objects rather than allocating a new
comment|// one.
DECL|field|pool
name|ConcurrentLinkedQueue
argument_list|<
name|T
argument_list|>
name|pool
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|maxObjects
name|int
name|maxObjects
decl_stmt|;
comment|/**    * Construct an allocator for objects of a certain type, keeping around a    * pool of up to<CODE>maxObjects</CODE> old objects.    *<P>    * Note that the pool size only restricts the number of objects that hang    * around when not needed, but<I>not</I> the maximum number of objects    * that are allocated when actually is use: If a number of concurrent    * threads ask for an allocation, all of them will get an object, even if     * their number is greater than maxObjects. If an application wants to    * limit the number of concurrent threads making allocations, it needs to    * do so on its own - for example by blocking new threads until the    * existing ones have finished. If more than maxObjects are freed, only    * maxObjects of them will be kept in the pool - the rest will not and    * will eventually be garbage-collected by Java.    *<P>    * In particular, when maxObjects=0, this object behaves as a trivial    * allocator, always allocating a new array and never reusing an old one.     */
DECL|method|TemporaryObjectAllocator
specifier|public
name|TemporaryObjectAllocator
parameter_list|(
name|int
name|maxObjects
parameter_list|)
block|{
name|this
operator|.
name|maxObjects
operator|=
name|maxObjects
expr_stmt|;
block|}
comment|/**    * Subclasses must override this method to actually create a new object    * of the desired type.    *     */
DECL|method|create
specifier|protected
specifier|abstract
name|T
name|create
parameter_list|()
function_decl|;
comment|/**    * Subclasses must override this method to clear an existing object of    * the desired type, to prepare it for reuse. Note that objects will be    * cleared just before reuse (on allocation), not when freed.    */
DECL|method|clear
specifier|protected
specifier|abstract
name|void
name|clear
parameter_list|(
name|T
name|object
parameter_list|)
function_decl|;
comment|/**    * Allocate a new object. If there's a previously allocated object in our    * pool, we return it immediately. Otherwise, a new object is allocated.    *<P>    * Don't forget to call {@link #free(Object)} when you're done with the object,    * to return it to the pool. If you don't, memory is<I>not</I> leaked,    * but the pool will remain empty and a new object will be allocated each    * time (just like the maxArrays=0 case).     */
DECL|method|allocate
specifier|public
specifier|final
name|T
name|allocate
parameter_list|()
block|{
name|T
name|object
init|=
name|pool
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|object
operator|==
literal|null
condition|)
block|{
return|return
name|create
argument_list|()
return|;
block|}
name|clear
argument_list|(
name|object
argument_list|)
expr_stmt|;
return|return
name|object
return|;
block|}
comment|/**    * Return a no-longer-needed object back to the pool. If we already have    * enough objects in the pool (maxObjects as specified in the constructor),    * the array will not be saved, and Java will eventually garbage collect    * it.    *<P>    * In particular, when maxArrays=0, the given array is never saved and    * free does nothing.    */
DECL|method|free
specifier|public
specifier|final
name|void
name|free
parameter_list|(
name|T
name|object
parameter_list|)
block|{
if|if
condition|(
name|pool
operator|.
name|size
argument_list|()
operator|<
name|maxObjects
operator|&&
name|object
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|add
argument_list|(
name|object
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

