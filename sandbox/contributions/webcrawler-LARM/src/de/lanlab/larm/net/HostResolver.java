begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|de.lanlab.larm.net
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|xxl
operator|.
name|collections
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|beanutils
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|*
import|;
end_import

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_comment
comment|//class LRUCache
end_comment

begin_comment
comment|//{
end_comment

begin_comment
comment|//    HashMap cache = null;
end_comment

begin_comment
comment|//    LinkedList order = null;
end_comment

begin_comment
comment|//    int max;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    public LRUCache(int max)
end_comment

begin_comment
comment|//    {
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        this.max = max;
end_comment

begin_comment
comment|//        cache = new HashMap((int)(max/0.6));
end_comment

begin_comment
comment|//        order = new LinkedList();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    public Object get(Object key)
end_comment

begin_comment
comment|//    {
end_comment

begin_comment
comment|//        return cache.get(key);
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    public void put(Object key, Object value)
end_comment

begin_comment
comment|//    {
end_comment

begin_comment
comment|//        if(!cache.containsKey(key))
end_comment

begin_comment
comment|//        {
end_comment

begin_comment
comment|//           if(order.size()> max)
end_comment

begin_comment
comment|//           {
end_comment

begin_comment
comment|//               cache.remove(order.removeLast());
end_comment

begin_comment
comment|//           }
end_comment

begin_comment
comment|//        }
end_comment

begin_comment
comment|//        else
end_comment

begin_comment
comment|//        {
end_comment

begin_comment
comment|//            //assert order.contains(key);
end_comment

begin_comment
comment|//            order.remove(key);
end_comment

begin_comment
comment|//            // quite expensive, probably need a hashed list
end_comment

begin_comment
comment|//            // or something even simpler
end_comment

begin_comment
comment|//        }
end_comment

begin_comment
comment|//        order.addFirst(key);
end_comment

begin_comment
comment|//        cache.put(key, value);
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//}
end_comment

begin_comment
comment|/**  * Uses @link{#resolveHost()} which transforms a host name according to the rules  * Rules are (and executed in this order)  *<ul>  *<li>if host starts with (startsWith), replace this part with (replacement)  *<li>if host ends with (endsWith), replace it with (replacement)  *<li>if host is (synonym), replace it with (replacement)  *</ul>  * the resolver can be configured through a property file, which is loaded by an  * Apache BeanUtils property loader.<p>  * Actually the resolver doesn't do any network calls, so this class can be used  * with any string, if you really need to  * @author Clemens Marschner  * @version 1.0  */
end_comment

begin_class
DECL|class|HostResolver
specifier|public
class|class
name|HostResolver
block|{
DECL|field|synonym
name|HashMap
name|synonym
decl_stmt|;
DECL|method|HostResolver
specifier|public
name|HostResolver
parameter_list|()
block|{
name|synonym
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * convenience method that loads the config from a properties file      * @param fileName a property file      * @throws IOException thrown if fileName is wrong or something went wrong while reading      * @throws InvocationTargetException thrown by java.util.Properties      * @throws IllegalAccessException thrown by java.util.Properties      */
DECL|method|initFromFile
specifier|public
name|void
name|initFromFile
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvocationTargetException
throws|,
name|IllegalAccessException
block|{
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|initFromProperties
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
comment|/**      * populates the synonym, startsWith and endsWith properties with a BeanUtils.populate()      * @param props      * @throws InvocationTargetException      * @throws IllegalAccessException      */
DECL|method|initFromProperties
specifier|public
name|void
name|initFromProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|InvocationTargetException
throws|,
name|IllegalAccessException
block|{
name|BeanUtils
operator|.
name|populate
argument_list|(
name|this
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
DECL|field|startsWithArray
name|ArrayList
name|startsWithArray
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|startsWithSize
name|int
name|startsWithSize
init|=
literal|0
decl_stmt|;
DECL|field|endsWithArray
name|ArrayList
name|endsWithArray
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|endsWithSize
name|int
name|endsWithSize
init|=
literal|0
decl_stmt|;
DECL|method|getStartsWith
specifier|public
name|String
name|getStartsWith
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IllegalAccessException
block|{
throw|throw
operator|new
name|IllegalAccessException
argument_list|(
literal|"brrffz"
argument_list|)
throw|;
block|}
DECL|method|setStartsWith
specifier|public
name|void
name|setStartsWith
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|rep
parameter_list|)
block|{
name|addHostStartsWithReplace
argument_list|(
name|name
operator|.
name|replace
argument_list|(
literal|','
argument_list|,
literal|'.'
argument_list|)
argument_list|,
name|rep
operator|.
name|replace
argument_list|(
literal|','
argument_list|,
literal|'.'
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getEndsWith
specifier|public
name|String
name|getEndsWith
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IllegalAccessException
block|{
throw|throw
operator|new
name|IllegalAccessException
argument_list|(
literal|"brrffz"
argument_list|)
throw|;
block|}
DECL|method|setEndsWith
specifier|public
name|void
name|setEndsWith
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|rep
parameter_list|)
block|{
name|this
operator|.
name|addHostEndsWithReplace
argument_list|(
name|name
operator|.
name|replace
argument_list|(
literal|','
argument_list|,
literal|'.'
argument_list|)
argument_list|,
name|rep
operator|.
name|replace
argument_list|(
literal|','
argument_list|,
literal|'.'
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setSynonym
specifier|public
name|void
name|setSynonym
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|syn
parameter_list|)
block|{
name|addSynonym
argument_list|(
name|name
operator|.
name|replace
argument_list|(
literal|','
argument_list|,
literal|'.'
argument_list|)
argument_list|,
name|syn
operator|.
name|replace
argument_list|(
literal|','
argument_list|,
literal|'.'
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getSynonym
specifier|public
name|String
name|getSynonym
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IllegalAccessException
block|{
throw|throw
operator|new
name|IllegalAccessException
argument_list|(
literal|"brrffz"
argument_list|)
throw|;
block|}
DECL|method|addSynonym
specifier|public
name|void
name|addSynonym
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|syn
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding synonym "
operator|+
name|name
operator|+
literal|" -> "
operator|+
name|syn
argument_list|)
expr_stmt|;
name|synonym
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|syn
argument_list|)
expr_stmt|;
block|}
comment|/**      * transforms a host name if a rule is found      * @param hostName      * @return probably changed host name      */
DECL|method|resolveHost
specifier|public
name|String
name|resolveHost
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
if|if
condition|(
name|hostName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|startsWithSize
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|test
init|=
operator|(
name|String
index|[]
operator|)
name|startsWithArray
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostName
operator|.
name|startsWith
argument_list|(
name|test
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|hostName
operator|=
name|test
index|[
literal|1
index|]
operator|+
name|hostName
operator|.
name|substring
argument_list|(
name|test
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|endsWithSize
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|test
init|=
operator|(
name|String
index|[]
operator|)
name|endsWithArray
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostName
operator|.
name|endsWith
argument_list|(
name|test
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|hostName
operator|=
name|hostName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hostName
operator|.
name|length
argument_list|()
operator|-
name|test
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
argument_list|)
operator|+
name|test
index|[
literal|1
index|]
expr_stmt|;
break|break;
block|}
block|}
name|String
name|syn
init|=
operator|(
name|String
operator|)
name|synonym
operator|.
name|get
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
return|return
name|syn
operator|!=
literal|null
condition|?
name|syn
else|:
name|hostName
return|;
block|}
DECL|method|addHostStartsWithReplace
specifier|public
name|void
name|addHostStartsWithReplace
parameter_list|(
name|String
name|startsWith
parameter_list|,
name|String
name|replace
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding sw replace "
operator|+
name|startsWith
operator|+
literal|" -> "
operator|+
name|replace
argument_list|)
expr_stmt|;
name|startsWithArray
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
name|startsWith
block|,
name|replace
block|}
argument_list|)
expr_stmt|;
name|startsWithSize
operator|++
expr_stmt|;
block|}
DECL|method|addHostEndsWithReplace
specifier|public
name|void
name|addHostEndsWithReplace
parameter_list|(
name|String
name|endsWith
parameter_list|,
name|String
name|replace
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding ew replace "
operator|+
name|endsWith
operator|+
literal|" -> "
operator|+
name|replace
argument_list|)
expr_stmt|;
name|endsWithArray
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
name|endsWith
block|,
name|replace
block|}
argument_list|)
expr_stmt|;
name|endsWithSize
operator|++
expr_stmt|;
block|}
comment|//    /** The pattern cache to compile and store patterns */
comment|//    private PatternCache __patternCache;
comment|//    /** The hashtable to cache higher-level expressions */
comment|//    private Cache __expressionCache;
comment|//    /** The pattern matcher to perform matching operations. */
comment|//    private Perl5Matcher __matcher = new Perl5Matcher();
comment|//
comment|//    public void addReplaceRegEx(String findRegEx, String replaceRegEx, boolean greedy)
comment|//    {
comment|//        int compileOptions    = Perl5Compiler.CASE_INSENSITIVE_MASK;
comment|//        int numSubstitutions = 1;
comment|//        if(greedy)
comment|//        {
comment|//            numSubstitutions = Util.SUBSTITUTE_ALL;
comment|//        }
comment|//
comment|//        Pattern compiledPattern = __patternCache.getPattern(findRegEx, compileOptions);
comment|//        Perl5Substitution substitution = new Perl5Substitution(replaceRegEx, numInterpolations);
comment|//        ParsedSubstitutionEntry entry = new ParsedSubstitutionEntry(compiledPattern, substitution,  numSubstitutions);
comment|//        __expressionCache.addElement(expression, entry);
comment|//
comment|//        result = Util.substitute(__matcher, compiledPattern, substitution,
comment|//                     input, numSubstitutions);
comment|//
comment|//        __lastMatch = __matcher.getMatch();
comment|//
comment|//        return result;
comment|//    }
block|}
end_class

end_unit

