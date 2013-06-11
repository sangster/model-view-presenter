package ca.ertt.mvp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MvpModel
{
    private final PropertyChangeSupport m_changeSupport;


    public MvpModel()
    {
        m_changeSupport = new PropertyChangeSupport( this );
    }


    public void addPropertyChangeListener( final PropertyChangeListener listener )
    {
        m_changeSupport.addPropertyChangeListener( listener );
    }


    public void addPropertyChangeListener( final String propertyName,
                                           final PropertyChangeListener listener )
    {
        m_changeSupport.addPropertyChangeListener( propertyName, listener );
    }


    public void removePropertyChangeListener( final PropertyChangeListener listener )
    {
        m_changeSupport.removePropertyChangeListener( listener );
    }


    public void removePropertyChangeListener( final String propertyName,
                                              final PropertyChangeListener listener )
    {
        m_changeSupport.removePropertyChangeListener( propertyName, listener );
    }


    protected void firePropertyChange( final String propertyName, final boolean oldValue,
                                       final boolean newValue )
    {
        m_changeSupport.firePropertyChange( propertyName, oldValue, newValue );
    }


    protected void firePropertyChange( final String propertyName, final int oldValue,
                                       final int newValue )
    {
        m_changeSupport.firePropertyChange( propertyName, oldValue, newValue );
    }


    protected void firePropertyChange( final PropertyChangeEvent event )
    {
        m_changeSupport.firePropertyChange( event );
    }


    protected void firePropertyChange( final String propertyName, final Object oldValue,
                                       final Object newValue )
    {
        m_changeSupport.firePropertyChange( propertyName, oldValue, newValue );
    }
}
