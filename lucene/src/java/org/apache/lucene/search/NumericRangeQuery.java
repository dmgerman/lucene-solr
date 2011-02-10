begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|NumericTokenStream
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericField
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ToStringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Terms
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|AttributeSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermsEnum
import|;
end_import

begin_comment
comment|/**  *<p>A {@link Query} that matches numeric values within a  * specified range.  To use this, you must first index the  * numeric values using {@link NumericField} (expert: {@link  * NumericTokenStream}).  If your terms are instead textual,  * you should use {@link TermRangeQuery}.  {@link  * NumericRangeFilter} is the filter equivalent of this  * query.</p>  *  *<p>You create a new NumericRangeQuery with the static  * factory methods, eg:  *  *<pre>  * Query q = NumericRangeQuery.newFloatRange("weight", 0.03f, 0.10f, true, true);  *</pre>  *  * matches all documents whose float valued "weight" field  * ranges from 0.03 to 0.10, inclusive.  *  *<p>The performance of NumericRangeQuery is much better  * than the corresponding {@link TermRangeQuery} because the  * number of terms that must be searched is usually far  * fewer, thanks to trie indexing, described below.</p>  *  *<p>You can optionally specify a<a  * href="#precisionStepDesc"><code>precisionStep</code></a>  * when creating this query.  This is necessary if you've  * changed this configuration from its default (4) during  * indexing.  Lower values consume more disk space but speed  * up searching.  Suitable values are between<b>1</b> and  *<b>8</b>. A good starting point to test is<b>4</b>,  * which is the default value for all<code>Numeric*</code>  * classes.  See<a href="#precisionStepDesc">below</a> for  * details.  *  *<p>This query defaults to {@linkplain  * MultiTermQuery#CONSTANT_SCORE_AUTO_REWRITE_DEFAULT} for  * 32 bit (int/float) ranges with precisionStep&le;8 and 64  * bit (long/double) ranges with precisionStep&le;6.  * Otherwise it uses {@linkplain  * MultiTermQuery#CONSTANT_SCORE_FILTER_REWRITE} as the  * number of terms is likely to be high.  With precision  * steps of&le;4, this query can be run with one of the  * BooleanQuery rewrite methods without changing  * BooleanQuery's default max clause count.  *  *<br><h3>How it works</h3>  *  *<p>See the publication about<a target="_blank" href="http://www.panfmp.org">panFMP</a>,  * where this algorithm was described (referred to as<code>TrieRangeQuery</code>):  *  *<blockquote><strong>Schindler, U, Diepenbroek, M</strong>, 2008.  *<em>Generic XML-based Framework for Metadata Portals.</em>  * Computers&amp; Geosciences 34 (12), 1947-1955.  *<a href="http://dx.doi.org/10.1016/j.cageo.2008.02.023"  * target="_blank">doi:10.1016/j.cageo.2008.02.023</a></blockquote>  *  *<p><em>A quote from this paper:</em> Because Apache Lucene is a full-text  * search engine and not a conventional database, it cannot handle numerical ranges  * (e.g., field value is inside user defined bounds, even dates are numerical values).  * We have developed an extension to Apache Lucene that stores  * the numerical values in a special string-encoded format with variable precision  * (all numerical values like doubles, longs, floats, and ints are converted to  * lexicographic sortable string representations and stored with different precisions  * (for a more detailed description of how the values are stored,  * see {@link NumericUtils}). A range is then divided recursively into multiple intervals for searching:  * The center of the range is searched only with the lowest possible precision in the<em>trie</em>,  * while the boundaries are matched more exactly. This reduces the number of terms dramatically.</p>  *  *<p>For the variant that stores long values in 8 different precisions (each reduced by 8 bits) that  * uses a lowest precision of 1 byte, the index contains only a maximum of 256 distinct values in the  * lowest precision. Overall, a range could consist of a theoretical maximum of  *<code>7*255*2 + 255 = 3825</code> distinct terms (when there is a term for every distinct value of an  * 8-byte-number in the index and the range covers almost all of them; a maximum of 255 distinct values is used  * because it would always be possible to reduce the full 256 values to one term with degraded precision).  * In practice, we have seen up to 300 terms in most cases (index with 500,000 metadata records  * and a uniform value distribution).</p>  *  *<a name="precisionStepDesc"><h3>Precision Step</h3>  *<p>You can choose any<code>precisionStep</code> when encoding values.  * Lower step values mean more precisions and so more terms in index (and index gets larger).  * On the other hand, the maximum number of terms to match reduces, which optimized query speed.  * The formula to calculate the maximum term count is:  *<pre>  *  n = [ (bitsPerValue/precisionStep - 1) * (2^precisionStep - 1 ) * 2 ] + (2^precisionStep - 1 )  *</pre>  *<p><em>(this formula is only correct, when<code>bitsPerValue/precisionStep</code> is an integer;  * in other cases, the value must be rounded up and the last summand must contain the modulo of the division as  * precision step)</em>.  * For longs stored using a precision step of 4,<code>n = 15*15*2 + 15 = 465</code>, and for a precision  * step of 2,<code>n = 31*3*2 + 3 = 189</code>. But the faster search speed is reduced by more seeking  * in the term enum of the index. Because of this, the ideal<code>precisionStep</code> value can only  * be found out by testing.<b>Important:</b> You can index with a lower precision step value and test search speed  * using a multiple of the original step value.</p>  *  *<p>Good values for<code>precisionStep</code> are depending on usage and data type:  *<ul>  *<li>The default for all data types is<b>4</b>, which is used, when no<code>precisionStep</code> is given.  *<li>Ideal value in most cases for<em>64 bit</em> data types<em>(long, double)</em> is<b>6</b> or<b>8</b>.  *<li>Ideal value in most cases for<em>32 bit</em> data types<em>(int, float)</em> is<b>4</b>.  *<li>For low cardinality fields larger precision steps are good. If the cardinality is&lt; 100, it is  *  fair to use {@link Integer#MAX_VALUE} (see below).  *<li>Steps<b>&ge;64</b> for<em>long/double</em> and<b>&ge;32</b> for<em>int/float</em> produces one token  *  per value in the index and querying is as slow as a conventional {@link TermRangeQuery}. But it can be used  *  to produce fields, that are solely used for sorting (in this case simply use {@link Integer#MAX_VALUE} as  *<code>precisionStep</code>). Using {@link NumericField NumericFields} for sorting  *  is ideal, because building the field cache is much faster than with text-only numbers.  *  These fields have one term per value and therefore also work with term enumeration for building distinct lists  *  (e.g. facets / preselected values to search for).  *  Sorting is also possible with range query optimized fields using one of the above<code>precisionSteps</code>.  *</ul>  *  *<p>Comparisons of the different types of RangeQueries on an index with about 500,000 docs showed  * that {@link TermRangeQuery} in boolean rewrite mode (with raised {@link BooleanQuery} clause count)  * took about 30-40 secs to complete, {@link TermRangeQuery} in constant score filter rewrite mode took 5 secs  * and executing this class took&lt;100ms to complete (on an Opteron64 machine, Java 1.5, 8 bit  * precision step). This query type was developed for a geographic portal, where the performance for  * e.g. bounding boxes or exact date/time stamps is important.</p>  *  * @since 2.9  **/
end_comment

begin_class
DECL|class|NumericRangeQuery
specifier|public
specifier|final
class|class
name|NumericRangeQuery
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|MultiTermQuery
block|{
DECL|method|NumericRangeQuery
specifier|private
name|NumericRangeQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
specifier|final
name|int
name|valSize
parameter_list|,
name|T
name|min
parameter_list|,
name|T
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|valSize
operator|==
literal|32
operator|||
name|valSize
operator|==
literal|64
operator|)
assert|;
if|if
condition|(
name|precisionStep
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep must be>=1"
argument_list|)
throw|;
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
name|this
operator|.
name|valSize
operator|=
name|valSize
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|minInclusive
operator|=
name|minInclusive
expr_stmt|;
name|this
operator|.
name|maxInclusive
operator|=
name|maxInclusive
expr_stmt|;
comment|// For bigger precisionSteps this query likely
comment|// hits too many terms, so set to CONSTANT_SCORE_FILTER right off
comment|// (especially as the FilteredTermEnum is costly if wasted only for AUTO tests because it
comment|// creates new enums from IndexReader for each sub-range)
switch|switch
condition|(
name|valSize
condition|)
block|{
case|case
literal|64
case|:
name|setRewriteMethod
argument_list|(
operator|(
name|precisionStep
operator|>
literal|6
operator|)
condition|?
name|CONSTANT_SCORE_FILTER_REWRITE
else|:
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
name|setRewriteMethod
argument_list|(
operator|(
name|precisionStep
operator|>
literal|8
operator|)
condition|?
name|CONSTANT_SCORE_FILTER_REWRITE
else|:
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// should never happen
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valSize must be 32 or 64"
argument_list|)
throw|;
block|}
comment|// shortcut if upper bound == lower bound
if|if
condition|(
name|min
operator|!=
literal|null
operator|&&
name|min
operator|.
name|equals
argument_list|(
name|max
argument_list|)
condition|)
block|{
name|setRewriteMethod
argument_list|(
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>long</code>    * range using the given<a href="#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newLongRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Long
argument_list|>
name|newLongRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Long
name|min
parameter_list|,
name|Long
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Long
argument_list|>
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|64
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>long</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newLongRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Long
argument_list|>
name|newLongRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Long
name|min
parameter_list|,
name|Long
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Long
argument_list|>
argument_list|(
name|field
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
argument_list|,
literal|64
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>int</code>    * range using the given<a href="#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newIntRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Integer
argument_list|>
name|newIntRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Integer
name|min
parameter_list|,
name|Integer
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|32
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>int</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newIntRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Integer
argument_list|>
name|newIntRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Integer
name|min
parameter_list|,
name|Integer
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|field
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
argument_list|,
literal|32
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>double</code>    * range using the given<a href="#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newDoubleRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Double
argument_list|>
name|newDoubleRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Double
name|min
parameter_list|,
name|Double
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Double
argument_list|>
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|64
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>double</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newDoubleRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Double
argument_list|>
name|newDoubleRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Double
name|min
parameter_list|,
name|Double
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Double
argument_list|>
argument_list|(
name|field
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
argument_list|,
literal|64
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>float</code>    * range using the given<a href="#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newFloatRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Float
argument_list|>
name|newFloatRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Float
name|min
parameter_list|,
name|Float
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Float
argument_list|>
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|32
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeQuery</code>, that queries a<code>float</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newFloatRange
specifier|public
specifier|static
name|NumericRangeQuery
argument_list|<
name|Float
argument_list|>
name|newFloatRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Float
name|min
parameter_list|,
name|Float
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeQuery
argument_list|<
name|Float
argument_list|>
argument_list|(
name|field
argument_list|,
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
argument_list|,
literal|32
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
specifier|final
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
comment|// very strange: java.lang.Number itsself is not Comparable, but all subclasses used here are
return|return
operator|(
name|min
operator|!=
literal|null
operator|&&
name|max
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|Comparable
argument_list|<
name|T
argument_list|>
operator|)
name|min
operator|)
operator|.
name|compareTo
argument_list|(
name|max
argument_list|)
operator|>
literal|0
operator|)
condition|?
name|TermsEnum
operator|.
name|EMPTY
else|:
operator|new
name|NumericRangeTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns<code>true</code> if the lower endpoint is inclusive */
DECL|method|includesMin
specifier|public
name|boolean
name|includesMin
parameter_list|()
block|{
return|return
name|minInclusive
return|;
block|}
comment|/** Returns<code>true</code> if the upper endpoint is inclusive */
DECL|method|includesMax
specifier|public
name|boolean
name|includesMax
parameter_list|()
block|{
return|return
name|maxInclusive
return|;
block|}
comment|/** Returns the lower value of this range query */
DECL|method|getMin
specifier|public
name|T
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
comment|/** Returns the upper value of this range query */
DECL|method|getMax
specifier|public
name|T
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
comment|/** Returns the precision step. */
DECL|method|getPrecisionStep
specifier|public
name|int
name|getPrecisionStep
parameter_list|()
block|{
return|return
name|precisionStep
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
name|sb
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
name|minInclusive
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|min
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|min
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|max
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|max
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|maxInclusive
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|o
operator|instanceof
name|NumericRangeQuery
condition|)
block|{
specifier|final
name|NumericRangeQuery
name|q
init|=
operator|(
name|NumericRangeQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
operator|(
name|q
operator|.
name|min
operator|==
literal|null
condition|?
name|min
operator|==
literal|null
else|:
name|q
operator|.
name|min
operator|.
name|equals
argument_list|(
name|min
argument_list|)
operator|)
operator|&&
operator|(
name|q
operator|.
name|max
operator|==
literal|null
condition|?
name|max
operator|==
literal|null
else|:
name|q
operator|.
name|max
operator|.
name|equals
argument_list|(
name|max
argument_list|)
operator|)
operator|&&
name|minInclusive
operator|==
name|q
operator|.
name|minInclusive
operator|&&
name|maxInclusive
operator|==
name|q
operator|.
name|maxInclusive
operator|&&
name|precisionStep
operator|==
name|q
operator|.
name|precisionStep
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|hash
operator|+=
name|precisionStep
operator|^
literal|0x64365465
expr_stmt|;
if|if
condition|(
name|min
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|min
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x14fa55fb
expr_stmt|;
if|if
condition|(
name|max
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|max
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x733fa5fe
expr_stmt|;
return|return
name|hash
operator|+
operator|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|minInclusive
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x14fa55fb
operator|)
operator|+
operator|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|maxInclusive
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x733fa5fe
operator|)
return|;
block|}
comment|// members (package private, to be also fast accessible by NumericRangeTermEnum)
DECL|field|precisionStep
DECL|field|valSize
specifier|final
name|int
name|precisionStep
decl_stmt|,
name|valSize
decl_stmt|;
DECL|field|min
DECL|field|max
specifier|final
name|T
name|min
decl_stmt|,
name|max
decl_stmt|;
DECL|field|minInclusive
DECL|field|maxInclusive
specifier|final
name|boolean
name|minInclusive
decl_stmt|,
name|maxInclusive
decl_stmt|;
comment|/**    * Subclass of FilteredTermsEnum for enumerating all terms that match the    * sub-ranges for trie range queries, using flex API.    *<p>    * WARNING: This term enumeration is not guaranteed to be always ordered by    * {@link Term#compareTo}.    * The ordering depends on how {@link NumericUtils#splitLongRange} and    * {@link NumericUtils#splitIntRange} generates the sub-ranges. For    * {@link MultiTermQuery} ordering is not relevant.    */
DECL|class|NumericRangeTermsEnum
specifier|private
specifier|final
class|class
name|NumericRangeTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|currentLowerBound
DECL|field|currentUpperBound
specifier|private
name|BytesRef
name|currentLowerBound
decl_stmt|,
name|currentUpperBound
decl_stmt|;
DECL|field|rangeBounds
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|BytesRef
argument_list|>
name|rangeBounds
init|=
operator|new
name|LinkedList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|termComp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
DECL|method|NumericRangeTermsEnum
name|NumericRangeTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|valSize
condition|)
block|{
case|case
literal|64
case|:
block|{
comment|// lower
name|long
name|minBound
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
if|if
condition|(
name|min
operator|instanceof
name|Long
condition|)
block|{
name|minBound
operator|=
name|min
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|min
operator|instanceof
name|Double
condition|)
block|{
name|minBound
operator|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|min
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|minInclusive
operator|&&
name|min
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|minBound
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
break|break;
name|minBound
operator|++
expr_stmt|;
block|}
comment|// upper
name|long
name|maxBound
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|max
operator|instanceof
name|Long
condition|)
block|{
name|maxBound
operator|=
name|max
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|max
operator|instanceof
name|Double
condition|)
block|{
name|maxBound
operator|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|max
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|maxInclusive
operator|&&
name|max
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|maxBound
operator|==
name|Long
operator|.
name|MIN_VALUE
condition|)
break|break;
name|maxBound
operator|--
expr_stmt|;
block|}
name|NumericUtils
operator|.
name|splitLongRange
argument_list|(
operator|new
name|NumericUtils
operator|.
name|LongRangeBuilder
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|addRange
parameter_list|(
name|BytesRef
name|minPrefixCoded
parameter_list|,
name|BytesRef
name|maxPrefixCoded
parameter_list|)
block|{
name|rangeBounds
operator|.
name|add
argument_list|(
name|minPrefixCoded
argument_list|)
expr_stmt|;
name|rangeBounds
operator|.
name|add
argument_list|(
name|maxPrefixCoded
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|precisionStep
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|32
case|:
block|{
comment|// lower
name|int
name|minBound
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
if|if
condition|(
name|min
operator|instanceof
name|Integer
condition|)
block|{
name|minBound
operator|=
name|min
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|min
operator|instanceof
name|Float
condition|)
block|{
name|minBound
operator|=
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|min
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|minInclusive
operator|&&
name|min
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|minBound
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
break|break;
name|minBound
operator|++
expr_stmt|;
block|}
comment|// upper
name|int
name|maxBound
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|max
operator|instanceof
name|Integer
condition|)
block|{
name|maxBound
operator|=
name|max
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|max
operator|instanceof
name|Float
condition|)
block|{
name|maxBound
operator|=
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|max
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|maxInclusive
operator|&&
name|max
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|maxBound
operator|==
name|Integer
operator|.
name|MIN_VALUE
condition|)
break|break;
name|maxBound
operator|--
expr_stmt|;
block|}
name|NumericUtils
operator|.
name|splitIntRange
argument_list|(
operator|new
name|NumericUtils
operator|.
name|IntRangeBuilder
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|addRange
parameter_list|(
name|BytesRef
name|minPrefixCoded
parameter_list|,
name|BytesRef
name|maxPrefixCoded
parameter_list|)
block|{
name|rangeBounds
operator|.
name|add
argument_list|(
name|minPrefixCoded
argument_list|)
expr_stmt|;
name|rangeBounds
operator|.
name|add
argument_list|(
name|maxPrefixCoded
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|precisionStep
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
comment|// should never happen
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valSize must be 32 or 64"
argument_list|)
throw|;
block|}
name|termComp
operator|=
name|getComparator
argument_list|()
expr_stmt|;
block|}
DECL|method|nextRange
specifier|private
name|void
name|nextRange
parameter_list|()
block|{
assert|assert
name|rangeBounds
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|0
assert|;
name|currentLowerBound
operator|=
name|rangeBounds
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
assert|assert
name|currentUpperBound
operator|==
literal|null
operator|||
name|termComp
operator|.
name|compare
argument_list|(
name|currentUpperBound
argument_list|,
name|currentLowerBound
argument_list|)
operator|<=
literal|0
operator|:
literal|"The current upper bound must be<= the new lower bound"
assert|;
name|currentUpperBound
operator|=
name|rangeBounds
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextSeekTerm
specifier|protected
specifier|final
name|BytesRef
name|nextSeekTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|rangeBounds
operator|.
name|size
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|nextRange
argument_list|()
expr_stmt|;
comment|// if the new upper bound is before the term parameter, the sub-range is never a hit
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|termComp
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|currentUpperBound
argument_list|)
operator|>
literal|0
condition|)
continue|continue;
comment|// never seek backwards, so use current term if lower bound is smaller
return|return
operator|(
name|term
operator|!=
literal|null
operator|&&
name|termComp
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|currentLowerBound
argument_list|)
operator|>
literal|0
operator|)
condition|?
name|term
else|:
name|currentLowerBound
return|;
block|}
comment|// no more sub-range enums available
assert|assert
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
assert|;
name|currentLowerBound
operator|=
name|currentUpperBound
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
specifier|final
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
while|while
condition|(
name|currentUpperBound
operator|==
literal|null
operator|||
name|termComp
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|currentUpperBound
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|AcceptStatus
operator|.
name|END
return|;
comment|// peek next sub-range, only seek if the current term is smaller than next lower bound
if|if
condition|(
name|termComp
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|rangeBounds
operator|.
name|getFirst
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
return|return
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
comment|// step forward to next range without seeking, as next lower range bound is less or equal current term
name|nextRange
argument_list|()
expr_stmt|;
block|}
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
block|}
block|}
end_class

end_unit

