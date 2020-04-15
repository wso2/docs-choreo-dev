class BaseConfig:
    """Set Flask configuration vars from .env file."""

    SQLALCHEMY_DATABASE_URI = 'mysql://choreo_performance_user@choreo-poc-db:choreo_performance_user@choreo-poc-db.mysql.database.azure.com/choreo_performance_db'

    SQLALCHEMY_ENGINE_OPTIONS = {
        'pool_size': 10,
        'pool_recycle': 90,
        'pool_timeout': 900,
        'max_overflow': 5,

    }

    SQLALCHEMY_TRACK_MODIFICATIONS = False
