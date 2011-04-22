begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|TokenStream
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_comment
comment|/**  * An AttributeSource contains a list of different {@link AttributeImpl}s,  * and methods to add and get them. There can only be a single instance  * of an attribute in the same AttributeSource instance. This is ensured  * by passing in the actual type of the Attribute (Class&lt;Attribute&gt;) to   * the {@link #addAttribute(Class)}, which then checks if an instance of  * that type is already present. If yes, it returns the instance, otherwise  * it creates a new instance and returns it.  */
end_comment

begin_class
DECL|class|AttributeSource
specifier|public
class|class
name|AttributeSource
block|{
comment|/**    * An AttributeFactory creates instances of {@link AttributeImpl}s.    */
DECL|class|AttributeFactory
specifier|public
specifier|static
specifier|abstract
class|class
name|AttributeFactory
block|{
comment|/**      * returns an {@link AttributeImpl} for the supplied {@link Attribute} interface class.      */
DECL|method|createAttributeInstance
specifier|public
specifier|abstract
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
function_decl|;
comment|/**      * This is the default factory that creates {@link AttributeImpl}s using the      * class name of the supplied {@link Attribute} interface class by appending<code>Impl</code> to it.      */
DECL|field|DEFAULT_ATTRIBUTE_FACTORY
specifier|public
specifier|static
specifier|final
name|AttributeFactory
name|DEFAULT_ATTRIBUTE_FACTORY
init|=
operator|new
name|DefaultAttributeFactory
argument_list|()
decl_stmt|;
DECL|class|DefaultAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|DefaultAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|attClassImplMap
specifier|private
specifier|static
specifier|final
name|WeakHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|,
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|>
argument_list|>
name|attClassImplMap
init|=
operator|new
name|WeakHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|,
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|DefaultAttributeFactory
specifier|private
name|DefaultAttributeFactory
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
try|try
block|{
return|return
name|getClassForInterface
argument_list|(
name|attClass
argument_list|)
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not instantiate implementing class for "
operator|+
name|attClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not instantiate implementing class for "
operator|+
name|attClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|getClassForInterface
specifier|private
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
name|getClassForInterface
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
synchronized|synchronized
init|(
name|attClassImplMap
init|)
block|{
specifier|final
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|>
name|ref
init|=
name|attClassImplMap
operator|.
name|get
argument_list|(
name|attClass
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
name|clazz
init|=
operator|(
name|ref
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|attClassImplMap
operator|.
name|put
argument_list|(
name|attClass
argument_list|,
operator|new
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|>
argument_list|(
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|attClass
operator|.
name|getName
argument_list|()
operator|+
literal|"Impl"
argument_list|,
literal|true
argument_list|,
name|attClass
operator|.
name|getClassLoader
argument_list|()
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|AttributeImpl
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not find implementing class for "
operator|+
name|attClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|clazz
return|;
block|}
block|}
block|}
block|}
comment|/**    * This class holds the state of an AttributeSource.    * @see #captureState    * @see #restoreState    */
DECL|class|State
specifier|public
specifier|static
specifier|final
class|class
name|State
implements|implements
name|Cloneable
block|{
DECL|field|attribute
name|AttributeImpl
name|attribute
decl_stmt|;
DECL|field|next
name|State
name|next
decl_stmt|;
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|State
name|clone
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|clone
operator|.
name|attribute
operator|=
operator|(
name|AttributeImpl
operator|)
name|attribute
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|next
operator|=
operator|(
name|State
operator|)
name|next
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
block|}
comment|// These two maps must always be in sync!!!
comment|// So they are private, final and read-only from the outside (read-only iterators)
DECL|field|attributes
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|,
name|AttributeImpl
argument_list|>
name|attributes
decl_stmt|;
DECL|field|attributeImpls
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|,
name|AttributeImpl
argument_list|>
name|attributeImpls
decl_stmt|;
DECL|field|currentState
specifier|private
specifier|final
name|State
index|[]
name|currentState
decl_stmt|;
DECL|field|factory
specifier|private
name|AttributeFactory
name|factory
decl_stmt|;
comment|/**    * An AttributeSource using the default attribute factory {@link AttributeSource.AttributeFactory#DEFAULT_ATTRIBUTE_FACTORY}.    */
DECL|method|AttributeSource
specifier|public
name|AttributeSource
parameter_list|()
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
expr_stmt|;
block|}
comment|/**    * An AttributeSource that uses the same attributes as the supplied one.    */
DECL|method|AttributeSource
specifier|public
name|AttributeSource
parameter_list|(
name|AttributeSource
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"input AttributeSource must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|attributes
operator|=
name|input
operator|.
name|attributes
expr_stmt|;
name|this
operator|.
name|attributeImpls
operator|=
name|input
operator|.
name|attributeImpls
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
name|input
operator|.
name|currentState
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|input
operator|.
name|factory
expr_stmt|;
block|}
comment|/**    * An AttributeSource using the supplied {@link AttributeFactory} for creating new {@link Attribute} instances.    */
DECL|method|AttributeSource
specifier|public
name|AttributeSource
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|attributes
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|,
name|AttributeImpl
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|attributeImpls
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|,
name|AttributeImpl
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
operator|new
name|State
index|[
literal|1
index|]
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
comment|/**    * returns the used AttributeFactory.    */
DECL|method|getAttributeFactory
specifier|public
specifier|final
name|AttributeFactory
name|getAttributeFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|factory
return|;
block|}
comment|/** Returns a new iterator that iterates the attribute classes    * in the same order they were added in.    */
DECL|method|getAttributeClassesIterator
specifier|public
specifier|final
name|Iterator
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
name|getAttributeClassesIterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|attributes
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** Returns a new iterator that iterates all unique Attribute implementations.    * This iterator may contain less entries that {@link #getAttributeClassesIterator},    * if one instance implements more than one Attribute interface.    */
DECL|method|getAttributeImplsIterator
specifier|public
specifier|final
name|Iterator
argument_list|<
name|AttributeImpl
argument_list|>
name|getAttributeImplsIterator
parameter_list|()
block|{
specifier|final
name|State
name|initState
init|=
name|getCurrentState
argument_list|()
decl_stmt|;
if|if
condition|(
name|initState
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|AttributeImpl
argument_list|>
argument_list|()
block|{
specifier|private
name|State
name|state
init|=
name|initState
decl_stmt|;
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|AttributeImpl
name|next
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
specifier|final
name|AttributeImpl
name|att
init|=
name|state
operator|.
name|attribute
decl_stmt|;
name|state
operator|=
name|state
operator|.
name|next
expr_stmt|;
return|return
name|att
return|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|state
operator|!=
literal|null
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
expr|<
name|AttributeImpl
operator|>
name|emptySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
comment|/** a cache that stores all interfaces for known implementation classes for performance (slow reflection) */
DECL|field|knownImplClasses
specifier|private
specifier|static
specifier|final
name|WeakHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|,
name|LinkedList
argument_list|<
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|knownImplClasses
init|=
operator|new
name|WeakHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
argument_list|,
name|LinkedList
argument_list|<
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getAttributeInterfaces
specifier|static
name|LinkedList
argument_list|<
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|>
name|getAttributeInterfaces
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
name|clazz
parameter_list|)
block|{
synchronized|synchronized
init|(
name|knownImplClasses
init|)
block|{
name|LinkedList
argument_list|<
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|>
name|foundInterfaces
init|=
name|knownImplClasses
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|foundInterfaces
operator|==
literal|null
condition|)
block|{
comment|// we have a strong reference to the class instance holding all interfaces in the list (parameter "att"),
comment|// so all WeakReferences are never evicted by GC
name|knownImplClasses
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|foundInterfaces
operator|=
operator|new
name|LinkedList
argument_list|<
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// find all interfaces that this attribute instance implements
comment|// and that extend the Attribute interface
name|Class
argument_list|<
name|?
argument_list|>
name|actClazz
init|=
name|clazz
decl_stmt|;
do|do
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|curInterface
range|:
name|actClazz
operator|.
name|getInterfaces
argument_list|()
control|)
block|{
if|if
condition|(
name|curInterface
operator|!=
name|Attribute
operator|.
name|class
operator|&&
name|Attribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|curInterface
argument_list|)
condition|)
block|{
name|foundInterfaces
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|(
name|curInterface
operator|.
name|asSubclass
argument_list|(
name|Attribute
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|actClazz
operator|=
name|actClazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|actClazz
operator|!=
literal|null
condition|)
do|;
block|}
return|return
name|foundInterfaces
return|;
block|}
block|}
comment|/**<b>Expert:</b> Adds a custom AttributeImpl instance with one or more Attribute interfaces.    *<p><font color="red"><b>Please note:</b> It is not guaranteed, that<code>att</code> is added to    * the<code>AttributeSource</code>, because the provided attributes may already exist.    * You should always retrieve the wanted attributes using {@link #getAttribute} after adding    * with this method and cast to your class.    * The recommended way to use custom implementations is using an {@link AttributeFactory}.    *</font></p>    */
DECL|method|addAttributeImpl
specifier|public
specifier|final
name|void
name|addAttributeImpl
parameter_list|(
specifier|final
name|AttributeImpl
name|att
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|AttributeImpl
argument_list|>
name|clazz
init|=
name|att
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|attributeImpls
operator|.
name|containsKey
argument_list|(
name|clazz
argument_list|)
condition|)
return|return;
specifier|final
name|LinkedList
argument_list|<
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
argument_list|>
name|foundInterfaces
init|=
name|getAttributeInterfaces
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
comment|// add all interfaces of this AttributeImpl to the maps
for|for
control|(
name|WeakReference
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
name|curInterfaceRef
range|:
name|foundInterfaces
control|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|curInterface
init|=
name|curInterfaceRef
operator|.
name|get
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|curInterface
operator|!=
literal|null
operator|)
operator|:
literal|"We have a strong reference on the class holding the interfaces, so they should never get evicted"
assert|;
comment|// Attribute is a superclass of this interface
if|if
condition|(
operator|!
name|attributes
operator|.
name|containsKey
argument_list|(
name|curInterface
argument_list|)
condition|)
block|{
comment|// invalidate state to force recomputation in captureState()
name|this
operator|.
name|currentState
index|[
literal|0
index|]
operator|=
literal|null
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|curInterface
argument_list|,
name|att
argument_list|)
expr_stmt|;
name|attributeImpls
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|att
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The caller must pass in a Class&lt;? extends Attribute&gt; value.    * This method first checks if an instance of that class is     * already in this AttributeSource and returns it. Otherwise a    * new instance is created, added to this AttributeSource and returned.     */
DECL|method|addAttribute
specifier|public
specifier|final
parameter_list|<
name|A
extends|extends
name|Attribute
parameter_list|>
name|A
name|addAttribute
parameter_list|(
name|Class
argument_list|<
name|A
argument_list|>
name|attClass
parameter_list|)
block|{
name|AttributeImpl
name|attImpl
init|=
name|attributes
operator|.
name|get
argument_list|(
name|attClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|attImpl
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|attClass
operator|.
name|isInterface
argument_list|()
operator|&&
name|Attribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"addAttribute() only accepts an interface that extends Attribute, but "
operator|+
name|attClass
operator|.
name|getName
argument_list|()
operator|+
literal|" does not fulfil this contract."
argument_list|)
throw|;
block|}
name|addAttributeImpl
argument_list|(
name|attImpl
operator|=
name|this
operator|.
name|factory
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|attClass
operator|.
name|cast
argument_list|(
name|attImpl
argument_list|)
return|;
block|}
comment|/** Returns true, iff this AttributeSource has any attributes */
DECL|method|hasAttributes
specifier|public
specifier|final
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
operator|!
name|this
operator|.
name|attributes
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * The caller must pass in a Class&lt;? extends Attribute&gt; value.     * Returns true, iff this AttributeSource contains the passed-in Attribute.    */
DECL|method|hasAttribute
specifier|public
specifier|final
name|boolean
name|hasAttribute
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
return|return
name|this
operator|.
name|attributes
operator|.
name|containsKey
argument_list|(
name|attClass
argument_list|)
return|;
block|}
comment|/**    * The caller must pass in a Class&lt;? extends Attribute&gt; value.     * Returns the instance of the passed in Attribute contained in this AttributeSource    *     * @throws IllegalArgumentException if this AttributeSource does not contain the    *         Attribute. It is recommended to always use {@link #addAttribute} even in consumers    *         of TokenStreams, because you cannot know if a specific TokenStream really uses    *         a specific Attribute. {@link #addAttribute} will automatically make the attribute    *         available. If you want to only use the attribute, if it is available (to optimize    *         consuming), use {@link #hasAttribute}.    */
DECL|method|getAttribute
specifier|public
specifier|final
parameter_list|<
name|A
extends|extends
name|Attribute
parameter_list|>
name|A
name|getAttribute
parameter_list|(
name|Class
argument_list|<
name|A
argument_list|>
name|attClass
parameter_list|)
block|{
name|AttributeImpl
name|attImpl
init|=
name|attributes
operator|.
name|get
argument_list|(
name|attClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|attImpl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This AttributeSource does not have the attribute '"
operator|+
name|attClass
operator|.
name|getName
argument_list|()
operator|+
literal|"'."
argument_list|)
throw|;
block|}
return|return
name|attClass
operator|.
name|cast
argument_list|(
name|attImpl
argument_list|)
return|;
block|}
DECL|method|getCurrentState
specifier|private
name|State
name|getCurrentState
parameter_list|()
block|{
name|State
name|s
init|=
name|currentState
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
operator|||
operator|!
name|hasAttributes
argument_list|()
condition|)
block|{
return|return
name|s
return|;
block|}
name|State
name|c
init|=
name|s
operator|=
name|currentState
index|[
literal|0
index|]
operator|=
operator|new
name|State
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|AttributeImpl
argument_list|>
name|it
init|=
name|attributeImpls
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|c
operator|.
name|attribute
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|c
operator|.
name|next
operator|=
operator|new
name|State
argument_list|()
expr_stmt|;
name|c
operator|=
name|c
operator|.
name|next
expr_stmt|;
name|c
operator|.
name|attribute
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * Resets all Attributes in this AttributeSource by calling    * {@link AttributeImpl#clear()} on each Attribute implementation.    */
DECL|method|clearAttributes
specifier|public
specifier|final
name|void
name|clearAttributes
parameter_list|()
block|{
for|for
control|(
name|State
name|state
init|=
name|getCurrentState
argument_list|()
init|;
name|state
operator|!=
literal|null
condition|;
name|state
operator|=
name|state
operator|.
name|next
control|)
block|{
name|state
operator|.
name|attribute
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Captures the state of all Attributes. The return value can be passed to    * {@link #restoreState} to restore the state of this or another AttributeSource.    */
DECL|method|captureState
specifier|public
specifier|final
name|State
name|captureState
parameter_list|()
block|{
specifier|final
name|State
name|state
init|=
name|this
operator|.
name|getCurrentState
argument_list|()
decl_stmt|;
return|return
operator|(
name|state
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|(
name|State
operator|)
name|state
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Restores this state by copying the values of all attribute implementations    * that this state contains into the attributes implementations of the targetStream.    * The targetStream must contain a corresponding instance for each argument    * contained in this state (e.g. it is not possible to restore the state of    * an AttributeSource containing a TermAttribute into a AttributeSource using    * a Token instance as implementation).    *<p>    * Note that this method does not affect attributes of the targetStream    * that are not contained in this state. In other words, if for example    * the targetStream contains an OffsetAttribute, but this state doesn't, then    * the value of the OffsetAttribute remains unchanged. It might be desirable to    * reset its value to the default, in which case the caller should first    * call {@link TokenStream#clearAttributes()} on the targetStream.       */
DECL|method|restoreState
specifier|public
specifier|final
name|void
name|restoreState
parameter_list|(
name|State
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
return|return;
do|do
block|{
name|AttributeImpl
name|targetImpl
init|=
name|attributeImpls
operator|.
name|get
argument_list|(
name|state
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetImpl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"State contains AttributeImpl of type "
operator|+
name|state
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" that is not in in this AttributeSource"
argument_list|)
throw|;
block|}
name|state
operator|.
name|attribute
operator|.
name|copyTo
argument_list|(
name|targetImpl
argument_list|)
expr_stmt|;
name|state
operator|=
name|state
operator|.
name|next
expr_stmt|;
block|}
do|while
condition|(
name|state
operator|!=
literal|null
condition|)
do|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|code
init|=
literal|0
decl_stmt|;
for|for
control|(
name|State
name|state
init|=
name|getCurrentState
argument_list|()
init|;
name|state
operator|!=
literal|null
condition|;
name|state
operator|=
name|state
operator|.
name|next
control|)
block|{
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|state
operator|.
name|attribute
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|code
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|AttributeSource
condition|)
block|{
name|AttributeSource
name|other
init|=
operator|(
name|AttributeSource
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|hasAttributes
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|other
operator|.
name|hasAttributes
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|attributeImpls
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|attributeImpls
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// it is only equal if all attribute impls are the same in the same order
name|State
name|thisState
init|=
name|this
operator|.
name|getCurrentState
argument_list|()
decl_stmt|;
name|State
name|otherState
init|=
name|other
operator|.
name|getCurrentState
argument_list|()
decl_stmt|;
while|while
condition|(
name|thisState
operator|!=
literal|null
operator|&&
name|otherState
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|otherState
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
operator|!=
name|thisState
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
operator|||
operator|!
name|otherState
operator|.
name|attribute
operator|.
name|equals
argument_list|(
name|thisState
operator|.
name|attribute
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|thisState
operator|=
name|thisState
operator|.
name|next
expr_stmt|;
name|otherState
operator|=
name|otherState
operator|.
name|next
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
operator|!
name|other
operator|.
name|hasAttributes
argument_list|()
return|;
block|}
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/**    * This method returns the current attribute values as a string in the following format    * by calling the {@link #reflectWith(AttributeReflector)} method:    *     *<ul>    *<li><em>iff {@code prependAttClass=true}:</em> {@code "AttributeClass#key=value,AttributeClass#key=value"}    *<li><em>iff {@code prependAttClass=false}:</em> {@code "key=value,key=value"}    *</ul>    *    * @see #reflectWith(AttributeReflector)    */
DECL|method|reflectAsString
specifier|public
specifier|final
name|String
name|reflectAsString
parameter_list|(
specifier|final
name|boolean
name|prependAttClass
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|reflectWith
argument_list|(
operator|new
name|AttributeReflector
argument_list|()
block|{
specifier|public
name|void
name|reflect
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prependAttClass
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|attClass
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|value
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * This method is for introspection of attributes, it should simply    * add the key/values this AttributeSource holds to the given {@link AttributeReflector}.    *    *<p>This method iterates over all Attribute implementations and calls the    * corresponding {@link AttributeImpl#reflectWith} method.</p>    *    * @see AttributeImpl#reflectWith    */
DECL|method|reflectWith
specifier|public
specifier|final
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
for|for
control|(
name|State
name|state
init|=
name|getCurrentState
argument_list|()
init|;
name|state
operator|!=
literal|null
condition|;
name|state
operator|=
name|state
operator|.
name|next
control|)
block|{
name|state
operator|.
name|attribute
operator|.
name|reflectWith
argument_list|(
name|reflector
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Performs a clone of all {@link AttributeImpl} instances returned in a new    * {@code AttributeSource} instance. This method can be used to e.g. create another TokenStream    * with exactly the same attributes (using {@link #AttributeSource(AttributeSource)}).    * You can also use it as a (non-performant) replacement for {@link #captureState}, if you need to look    * into / modify the captured state.    */
DECL|method|cloneAttributes
specifier|public
specifier|final
name|AttributeSource
name|cloneAttributes
parameter_list|()
block|{
specifier|final
name|AttributeSource
name|clone
init|=
operator|new
name|AttributeSource
argument_list|(
name|this
operator|.
name|factory
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasAttributes
argument_list|()
condition|)
block|{
comment|// first clone the impls
for|for
control|(
name|State
name|state
init|=
name|getCurrentState
argument_list|()
init|;
name|state
operator|!=
literal|null
condition|;
name|state
operator|=
name|state
operator|.
name|next
control|)
block|{
name|clone
operator|.
name|attributeImpls
operator|.
name|put
argument_list|(
name|state
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
argument_list|,
operator|(
name|AttributeImpl
operator|)
name|state
operator|.
name|attribute
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// now the interfaces
for|for
control|(
name|Entry
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|,
name|AttributeImpl
argument_list|>
name|entry
range|:
name|this
operator|.
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|clone
operator|.
name|attributes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|clone
operator|.
name|attributeImpls
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|clone
return|;
block|}
comment|/**    * Copies the contents of this {@code AttributeSource} to the given target {@code AttributeSource}.    * The given instance has to provide all {@link Attribute}s this instance contains.     * The actual attribute implementations must be identical in both {@code AttributeSource} instances;    * ideally both AttributeSource instances should use the same {@link AttributeFactory}.    * You can use this method as a replacement for {@link #restoreState}, if you use    * {@link #cloneAttributes} instead of {@link #captureState}.    */
DECL|method|copyTo
specifier|public
specifier|final
name|void
name|copyTo
parameter_list|(
name|AttributeSource
name|target
parameter_list|)
block|{
for|for
control|(
name|State
name|state
init|=
name|getCurrentState
argument_list|()
init|;
name|state
operator|!=
literal|null
condition|;
name|state
operator|=
name|state
operator|.
name|next
control|)
block|{
specifier|final
name|AttributeImpl
name|targetImpl
init|=
name|target
operator|.
name|attributeImpls
operator|.
name|get
argument_list|(
name|state
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetImpl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This AttributeSource contains AttributeImpl of type "
operator|+
name|state
operator|.
name|attribute
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" that is not in the target"
argument_list|)
throw|;
block|}
name|state
operator|.
name|attribute
operator|.
name|copyTo
argument_list|(
name|targetImpl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

