package ca.ertt.mvp;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MvpPresenter<Model extends MvpModel, View extends MvpView>
        implements PropertyChangeListener
{
    private final List<View> m_views = new CopyOnWriteArrayList<View>();
    private final Model m_model;


    public MvpPresenter( final Model model )
    {
        m_model = model;
        m_model.addPropertyChangeListener( this );
    }


    public List<View> getViews()
    {
        return m_views;
    }


    public void addView( final View view )
    {
        m_views.add( view );
    }


    public boolean removeView( final View view )
    {
        return m_views.remove( view );
    }


    public Model getModel()
    {
        return m_model;
    }


    @Override
    public final void propertyChange( final PropertyChangeEvent event )
    {
        final String property = event.getPropertyName();

        if( property == null ) {
            otherPropertyChange( event );
            return;
        }

        boolean anyHandlers = false;
        for( final Method method : getClass().getDeclaredMethods() ) {
            if( doesMethodHandleProperty( method, property ) ) {
                anyHandlers = true;
                try {
                    invokeMethod( method, getExpectedArgument( method, event ) );
                } catch( final Exception e ) {
                    throw new RuntimeException( "Could not call method: " + method, e );
                }
            }
        }

        if( !anyHandlers ) {
            otherPropertyChange( event );
        }
    }


    private void invokeMethod( final Method method, final Object arg )
            throws InvocationTargetException, IllegalAccessException
    {
        final boolean oldAccessible = method.isAccessible();
        method.setAccessible( true );
        if( arg == null ) {
            method.invoke( this );
        } else {
            method.invoke( this, arg );
        }
        method.setAccessible( oldAccessible );
    }


    @SuppressWarnings( "unused" )
    protected void otherPropertyChange( final PropertyChangeEvent event )
    {
    }


    private boolean doesMethodHandleProperty( final Method method, final String property )
    {
        if( method.getParameterTypes().length > 1 ) {
            return false;
        }

        final HandlesPropertyChange ann = method.getAnnotation( HandlesPropertyChange.class );
        if( ann == null ) {
            return false;
        }

        for( final String annotatedProperty : ann.value() ) {
            if( property.equals( annotatedProperty ) ) {
                return true;
            }
        }

        return false;
    }


    private Object getExpectedArgument( final Method method, final PropertyChangeEvent event )
    {
        final Class<?>[] parameters = method.getParameterTypes();
        if( parameters.length == 0 ) {
            return null;
        }

        if( PropertyChangeEvent.class.isAssignableFrom( parameters[0] ) ) {
            return event;
        }

        return event.getSource();
    }


    protected void callOnAllViews( final String methodName, final Object... args )
    {
        if( getViews().isEmpty() ) {
            return; // nothing to call on
        }
        final Method method = getMethod( getViews().get( 0 ).getClass(), methodName );

        if( EventQueue.isDispatchThread() ) {
            callMethodOnAllViews( method, args );
        } else {
            EventQueue.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    callMethodOnAllViews( method, args );
                }
            } );
        }
    }


    private void callMethodOnAllViews( final Method method, final Object[] args )
    {
        for( final MvpView view : getViews() ) {
            try {
                method.invoke( view, args );
            } catch( Exception e ) {
                throw new RuntimeException( e );
            }
        }
    }


    private Method getMethod( final Class<?> klass, final String methodName )
    {
        for( final Method m : klass.getMethods() ) {
            if( m.getName().equals( methodName ) ) {
                return m;
            }
        }
        throw new RuntimeException( String.format( "Could not find method in \"%s\" named \"%s\"",
                                                   klass.getSimpleName(), methodName ) );
    }
}
